package com.tapisdev.penjualankasir.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.tapisdev.penjualankasir.MainActivity
import com.tapisdev.penjualankasir.R
import com.tapisdev.penjualankasir.activity.ProfileActivity
import com.tapisdev.penjualankasir.activity.TambahPelangganActivity
import com.tapisdev.penjualankasir.adapter.AdapterBarang
import com.tapisdev.penjualankasir.adapter.AdapterPelanggan
import com.tapisdev.penjualankasir.databinding.FragmentHomeBinding
import com.tapisdev.penjualankasir.model.Barang
import com.tapisdev.penjualankasir.model.Pelanggan
import com.tapisdev.penjualankasir.model.UserPreference
import com.tapisdev.penjualankasir.response.BarangResponse
import com.tapisdev.penjualankasir.response.PelangganResponse
import com.tapisdev.penjualankasir.util.ApiMain
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    //lateinit var binding_shimmer : ShimmerSuratBinding
    lateinit var shimmerFrameLayout : ShimmerFrameLayout
    lateinit var sflBarang : ShimmerFrameLayout
    lateinit var mUserPref : UserPreference
    lateinit var adapter : AdapterPelanggan
    lateinit var adapterBarang : AdapterBarang
    var listPelanggan = ArrayList<Pelanggan>()
    var listBarang = ArrayList<Barang>()

    var CURRENT_PAGE = 1
    var NEXT_PAGE = CURRENT_PAGE + 1
    var TAG_GET_PELANGGAN = "pelanggan"
    var TAG_GET_BARANG = "pelanggan"
    var TAG_GET_MORE_PELANGGAN = "morepelanggan"
    var KATA_KUNCI = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        shimmerFrameLayout = root.findViewById(R.id.sflMain)
        sflBarang = root.findViewById(R.id.sflHorizontal)
        mUserPref = UserPreference(requireContext())
        adapter = AdapterPelanggan(listPelanggan)
        adapterBarang = AdapterBarang(listBarang)

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPelanggan.setHasFixedSize(true)
        binding.rvPelanggan.layoutManager = layoutManager
        binding.rvPelanggan.adapter = adapter

        val horizontallayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBarang.setHasFixedSize(true)
        binding.rvBarang.layoutManager = horizontallayoutManager
        binding.rvBarang.adapter = adapterBarang



        binding.btnTambahPelanggan.setOnClickListener {
            val i = Intent(requireActivity(),TambahPelangganActivity::class.java)
            startActivity(i)
        }
        binding.ivProfil.setOnClickListener {
            val i = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(i)
        }

        updateUI()
        getDataPelanggan()
        getDataBarang()
        return root
    }

    fun updateUI(){
        binding.tvNamaUmkm.setText(mUserPref.getNamaUmkm())
    }

    fun resetPagination(){
        CURRENT_PAGE = 1
        NEXT_PAGE = CURRENT_PAGE + 1
    }

    fun getDataPelanggan(){
        showLoadingShimmerPelanggan()
        resetPagination()

        ApiMain().services.getPelanggan(mUserPref.getToken(),CURRENT_PAGE,KATA_KUNCI).enqueue(object :
            retrofit2.Callback<PelangganResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<PelangganResponse>, response: Response<PelangganResponse>) {
                //Tulis code jika response sukses
                Log.d(TAG_GET_PELANGGAN,response.toString())
                Log.d(TAG_GET_PELANGGAN,"http status : "+response.code())

                if(response.code() == 200) {
                    listPelanggan.clear()
                    response.body()?.data_pelanggan?.let {
                        Log.d(TAG_GET_PELANGGAN,"dari API : "+it)
                        Log.d(TAG_GET_PELANGGAN,"jumlah dari API : "+it.size)
                        listPelanggan.addAll(it)
                        adapter.notifyDataSetChanged()

                        hideLoadingShimmerPelanggan()
                        Log.d(TAG_GET_PELANGGAN,"isi adapter  : "+adapter.itemCount)
                    }

                    if (listPelanggan.size == 0){
                        binding.tvInfoEmpty.visibility = View.VISIBLE
                    }

                }else {
                    Toasty.error(requireContext(), "gagal mengambil data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_PELANGGAN,"err :"+response.message())
                }
            }
            override fun onFailure(call: Call<PelangganResponse>, t: Throwable){
                //Tulis code jika response fail
                val errMsg = t.message.toString()
                if (errMsg.takeLast(6).equals("$.null")){
                    Log.d(TAG_GET_PELANGGAN,"rusak nya gpapa kok  ")
                    hideLoadingShimmerPelanggan()
                }else{
                    Toasty.error(requireContext(), "response failure for more data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_PELANGGAN,"rusak : "+t.message.toString())
                }
            }
        })
    }

    fun getDataBarang(){
        showLoadingShimmerBarang()
        resetPagination()

        ApiMain().services.getBarang(mUserPref.getToken(),CURRENT_PAGE,KATA_KUNCI).enqueue(object :
            retrofit2.Callback<BarangResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<BarangResponse>, response: Response<BarangResponse>) {
                //Tulis code jika response sukses
                Log.d(TAG_GET_BARANG,response.toString())
                Log.d(TAG_GET_BARANG,"http status : "+response.code())

                if(response.code() == 200) {
                    listBarang.clear()
                    response.body()?.data_barang?.let {
                        Log.d(TAG_GET_BARANG,"dari API : "+it)
                        Log.d(TAG_GET_BARANG,"jumlah dari API : "+it.size)
                        listBarang.addAll(it)
                        adapterBarang.notifyDataSetChanged()

                        hideLoadingShimmerBarang()
                        Log.d(TAG_GET_BARANG,"isi adapter  : "+adapterBarang.itemCount)
                    }

                    if (listBarang.size == 0){
                        binding.tvInfoEmpty.visibility = View.VISIBLE
                    }

                }else {
                    Toasty.error(requireContext(), "gagal mengambil data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_BARANG,"err :"+response.message())
                }
            }
            override fun onFailure(call: Call<BarangResponse>, t: Throwable){
                //Tulis code jika response fail
                val errMsg = t.message.toString()
                if (errMsg.takeLast(6).equals("$.null")){
                    Log.d(TAG_GET_BARANG,"rusak nya gpapa kok  ")
                    hideLoadingShimmerBarang()
                }else{
                    Toasty.error(requireContext(), "response failure for more data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_BARANG,"rusak : "+t.message.toString())
                }
            }
        })
    }


    fun showLoadingShimmerPelanggan(){
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmerAnimation()

        binding.rvPelanggan.visibility = View.GONE
        binding.tvInfoEmpty.visibility = View.GONE
    }

    fun hideLoadingShimmerPelanggan(){
        if (shimmerFrameLayout.isVisible){
            shimmerFrameLayout.stopShimmerAnimation()
            shimmerFrameLayout.clearAnimation()
            shimmerFrameLayout.visibility = View.GONE
        }

        binding.rvPelanggan.visibility = View.VISIBLE
    }

    fun showLoadingShimmerBarang(){
        sflBarang.visibility = View.VISIBLE
        sflBarang.startShimmerAnimation()

        binding.rvBarang.visibility = View.GONE
        binding.tvInfoEmptyBarang.visibility = View.GONE
    }

    fun hideLoadingShimmerBarang(){
        if (sflBarang.isVisible){
            sflBarang.stopShimmerAnimation()
            sflBarang.clearAnimation()
            sflBarang.visibility = View.GONE
        }

        binding.rvBarang.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        getDataPelanggan()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): HomeFragment{
            val fragment = HomeFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}