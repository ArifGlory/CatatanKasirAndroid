package com.tapisdev.penjualankasir.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tapisdev.penjualankasir.BuildConfig
import com.tapisdev.penjualankasir.R
import com.tapisdev.penjualankasir.activity.TambahBarangActivity
import com.tapisdev.penjualankasir.adapter.AdapterBarang
import com.tapisdev.penjualankasir.adapter.AdapterTransaksi
import com.tapisdev.penjualankasir.databinding.FragmentHomeBinding
import com.tapisdev.penjualankasir.databinding.FragmentHutangBinding
import com.tapisdev.penjualankasir.databinding.FragmentStokBinding
import com.tapisdev.penjualankasir.databinding.FragmentUntungBinding
import com.tapisdev.penjualankasir.model.Barang
import com.tapisdev.penjualankasir.model.Transaksi
import com.tapisdev.penjualankasir.model.UserPreference
import com.tapisdev.penjualankasir.response.TotalUntungResponse
import com.tapisdev.penjualankasir.response.UntungResponse
import com.tapisdev.penjualankasir.util.ApiMain
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class UntungFragment : Fragment() {

    private var _binding: FragmentUntungBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    //lateinit var binding_shimmer : ShimmerSuratBinding
    lateinit var shimmerFrameLayout : ShimmerFrameLayout
    lateinit var mUserPref : UserPreference
    lateinit var adapter : AdapterTransaksi

    var listTransaksi = ArrayList<Transaksi>()
    val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
    val df = nf as DecimalFormat


    var CURRENT_PAGE = 1
    var NEXT_PAGE = CURRENT_PAGE + 1
    var TAG_GET_TRANSAKSI = "transaksi"
    var TAG_GET_REPORT = "reporttransaksi"
    var TAG_GET_TOTALUNTUNG = "totaluntung"
    var TAG_GET_MORE_TRANSAKSI = "moretransaksi"
    var dari = ""
    var sampai = ""
    var totalUntung = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUntungBinding.inflate(inflater, container, false)
        val root: View = binding.root

        shimmerFrameLayout = root.findViewById(R.id.sflMain)
        mUserPref = UserPreference(requireContext())
        adapter = AdapterTransaksi(listTransaksi)

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvUntung.setHasFixedSize(true)
        binding.rvUntung.layoutManager = layoutManager
        binding.rvUntung.adapter = adapter


        binding.cardLaporan.setOnClickListener {
            showDialogFilter()
        }
        binding.fabDownload.setOnClickListener {
            if (listTransaksi.size > 0){
                val base_url = BuildConfig.BASE_URL+"transaksi/report/print?token="
                val token = mUserPref.getToken()
                val downloadURL = base_url+token+"&dari="+dari+"&sampai="+sampai

                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(downloadURL)
                startActivity(i)
            }else{
                Toasty.error(requireContext(), "data transaksi masih kosong", Toast.LENGTH_SHORT, true).show()
            }
        }


        getDataUntung()
        //getTotalUntung()
        return root
    }

    fun showDialogFilter(){
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_select_time_untung, null)

        var selected_tgl_awal = ""
        var selected_tgl_akhir = ""
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val btnPilihAwal = view.findViewById<Button>(R.id.btnPilihAwal)
        val btnPilihAkhir = view.findViewById<Button>(R.id.btnPilihAkhir)
        val btnFilter = view.findViewById<Button>(R.id.btnFilter)
        val etTanggalAwal = view.findViewById<EditText>(R.id.etTanggalAwal)
        val etTanggalAkhir = view.findViewById<EditText>(R.id.etTanggalAkhir)

        btnPilihAwal.setOnClickListener {
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                etTanggalAwal.setText("" + dayOfMonth + "/" + (monthOfYear + 1 ) + "/" + year)
                //selected_tgl_awal = ""+dayOfMonth+"/"+(monthOfYear+1)+"/"+year
                selected_tgl_awal = ""+year+"-"+(monthOfYear+1)+"-"+dayOfMonth

            }, year, month, day)

            dpd.show()
        }
        btnPilihAkhir.setOnClickListener {
            val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                etTanggalAkhir.setText("" + dayOfMonth + "/" + (monthOfYear + 1 ) + "/" + year)
                //selected_tgl_akhir = ""+dayOfMonth+"/"+(monthOfYear+1)+"/"+year
                selected_tgl_akhir = ""+year+"-"+(monthOfYear+1)+"-"+dayOfMonth

            }, year, month, day)

            dpd.show()
        }
        btnFilter.setOnClickListener {
            filterDataUntung(selected_tgl_awal,selected_tgl_akhir)
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    fun filterDataUntung(tgl_mulai : String,tgl_akhir : String){
        Log.d(TAG_GET_REPORT," tgl_mulai "+tgl_mulai+ " dan tgl akir "+tgl_akhir)
        resetPagination()
        showLoadingShimmer()

        ApiMain().services.getReportUntung(mUserPref.getToken(),tgl_mulai,tgl_akhir).enqueue(object :
            retrofit2.Callback<UntungResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<UntungResponse>, response: Response<UntungResponse>) {
                //Tulis code jika response sukses
                Log.d(TAG_GET_REPORT,response.toString())
                Log.d(TAG_GET_REPORT,"http status : "+response.code())

                if(response.code() == 200) {
                    listTransaksi.clear()
                    response.body()?.data_untung?.let {
                        Log.d(TAG_GET_REPORT,"dari API : "+it)
                        Log.d(TAG_GET_REPORT,"jumlah dari API : "+it.size)
                        listTransaksi.addAll(it)
                        adapter.notifyDataSetChanged()

                        hideLoadingShimmer()
                        Log.d(TAG_GET_REPORT,"isi adapter  : "+adapter.itemCount)
                    }

                    if (listTransaksi.size == 0){
                        Toasty.info(requireContext(), "Belum ada data untuk rentang waktu ini..", Toast.LENGTH_SHORT, true).show()
                        binding.tvInfoEmpty.visibility = View.VISIBLE
                    }else{
                        dari = tgl_mulai
                        sampai = tgl_akhir
                        binding.fabDownload.visibility = View.VISIBLE

                        for (i in 0 until listTransaksi.size){
                            val untung = listTransaksi.get(i).total_untung
                            totalUntung = totalUntung + untung!!
                        }
                        binding.tvKeuntunganTotal.setText("Rp. "+df.format(totalUntung))

                    }

                }else {
                    Toasty.error(requireContext(), "gagal mengambil data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_REPORT,"err :"+response.message())
                }
            }
            override fun onFailure(call: Call<UntungResponse>, t: Throwable){
                //Tulis code jika response fail
                val errMsg = t.message.toString()
                if (errMsg.takeLast(6).equals("$.null")){
                    Log.d(TAG_GET_REPORT,"rusak nya gpapa kok  ")
                    hideLoadingShimmer()
                }else{
                    Toasty.error(requireContext(), "response failure for more data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_REPORT,"rusak : "+t.message.toString())
                }
            }
        })
    }

    fun resetPagination(){
        CURRENT_PAGE = 1
        NEXT_PAGE = CURRENT_PAGE + 1
    }

    fun getDataUntung(){
        showLoadingShimmer()
        resetPagination()

        ApiMain().services.historyTransaksi(mUserPref.getToken(),CURRENT_PAGE).enqueue(object :
            retrofit2.Callback<UntungResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<UntungResponse>, response: Response<UntungResponse>) {
                //Tulis code jika response sukses
                Log.d(TAG_GET_TRANSAKSI,response.toString())
                Log.d(TAG_GET_TRANSAKSI,"http status : "+response.code())

                if(response.code() == 200) {
                    listTransaksi.clear()
                    response.body()?.data_untung?.let {
                        Log.d(TAG_GET_TRANSAKSI,"dari API : "+it)
                        Log.d(TAG_GET_TRANSAKSI,"jumlah dari API : "+it.size)
                        listTransaksi.addAll(it)
                        adapter.notifyDataSetChanged()

                        hideLoadingShimmer()
                        Log.d(TAG_GET_TRANSAKSI,"isi adapter  : "+adapter.itemCount)
                    }

                    if (listTransaksi.size == 0){
                        binding.tvInfoEmpty.visibility = View.VISIBLE
                    }

                }else {
                    Toasty.error(requireContext(), "gagal mengambil data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_TRANSAKSI,"err :"+response.message())
                }
            }
            override fun onFailure(call: Call<UntungResponse>, t: Throwable){
                //Tulis code jika response fail
                val errMsg = t.message.toString()
                if (errMsg.takeLast(6).equals("$.null")){
                    Log.d(TAG_GET_TRANSAKSI,"rusak nya gpapa kok  ")
                    hideLoadingShimmer()
                }else{
                    Toasty.error(requireContext(), "response failure for more data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_TRANSAKSI,"rusak : "+t.message.toString())
                }
            }
        })
    }

    fun getTotalUntung(){
        binding.progressBar.visibility = View.VISIBLE

        ApiMain().services.getTotalUntung(mUserPref.getToken()).enqueue(object :
            retrofit2.Callback<TotalUntungResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<TotalUntungResponse>, response: Response<TotalUntungResponse>) {
                //Tulis code jika response sukses
                Log.d(TAG_GET_TRANSAKSI,response.toString())
                Log.d(TAG_GET_TRANSAKSI,"http status : "+response.code())


                if(response.code() == 200) {
                    val responAPI = response.body()
                    Log.d(TAG_GET_TOTALUNTUNG,"respon api : "+responAPI.toString())
                    val totalUntung = responAPI!!.data
                    val total =  totalUntung.toInt()
                    Log.d(TAG_GET_TOTALUNTUNG,"hasil : "+totalUntung)
                    binding.progressBar.visibility = View.GONE
                    binding.tvKeuntunganTotal.setText("Rp. "+df.format(total))
                    binding.tvKeuntunganTotal.visibility = View.VISIBLE


                }else {
                    binding.progressBar.visibility = View.GONE
                    Toasty.error(requireContext(), "gagal mengambil data total untung", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_TRANSAKSI,"err :"+response.message())
                }
            }
            override fun onFailure(call: Call<TotalUntungResponse>, t: Throwable){
                //Tulis code jika response fail
                binding.progressBar.visibility = View.GONE
                val errMsg = t.message.toString()
                if (errMsg.takeLast(6).equals("$.null")){
                    Log.d(TAG_GET_TRANSAKSI,"rusak nya gpapa kok  ")
                }else{
                    Toasty.error(requireContext(), "response failure for more data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_TRANSAKSI,"rusak : "+t.message.toString())
                }
            }
        })
    }


    fun showLoadingShimmer(){
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmerAnimation()

        binding.rvUntung.visibility = View.GONE
        binding.tvInfoEmpty.visibility = View.GONE
    }

    fun hideLoadingShimmer(){
        if (shimmerFrameLayout.isVisible){
            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.clearAnimation()
            shimmerFrameLayout.visibility = View.GONE
        }

        binding.rvUntung.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): UntungFragment{
            val fragment = UntungFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}