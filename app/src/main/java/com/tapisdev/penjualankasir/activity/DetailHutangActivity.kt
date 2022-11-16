package com.tapisdev.penjualankasir.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.penjualankasir.R
import com.tapisdev.penjualankasir.adapter.AdapterCicilanHutang
import com.tapisdev.penjualankasir.databinding.ActivityDetailHutangBinding
import com.tapisdev.penjualankasir.model.*
import com.tapisdev.penjualankasir.response.CommonResponse
import com.tapisdev.penjualankasir.response.CicilanHutangResponse
import com.tapisdev.penjualankasir.util.ApiMain
import es.dmoral.toasty.Toasty
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class DetailHutangActivity : BaseActivity() {
    lateinit var binding : ActivityDetailHutangBinding
    lateinit var hutang : Hutang
    lateinit var i : Intent
    val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
    val df = nf as DecimalFormat
    
    var TAG_UPDATE_HUTANG = "updatehutang"
    var TAG_GET_CICILAN = "getcicilan"

    var listCicilanHutang = ArrayList<CicilanHutang>()
    lateinit var adapter : AdapterCicilanHutang

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHutangBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mUserPref = UserPreference(this)
        adapter = AdapterCicilanHutang(listCicilanHutang)

        i = intent
        hutang = i.getParcelableExtra<Hutang>("hutang")!!

        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCicilan.setHasFixedSize(true)
        binding.rvCicilan.layoutManager = layoutManager
        binding.rvCicilan.adapter = adapter

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.rbSudahLunas.setOnCheckedChangeListener { compoundButton, b ->
            if (b == true){
                hutang.status = "lunas"
            }
        }
        binding.rbBelumLunas.setOnCheckedChangeListener { compoundButton, b ->
            if (b == true){
                hutang.status = "belum lunas"
            }
        }
        binding.btnSimpan.setOnClickListener {
            checkValidation()
        }

        updateUI()
        getDataCicilan()
    }

    fun updateUI(){
        binding.etHutang.isEnabled = false
        binding.etHutang.setText("Rp. "+df.format(hutang.hutang))

        var nama_pelanggan = ""
        if (hutang.nama_pelanggan == null || hutang.nama_pelanggan.equals("")){
            nama_pelanggan = "Guest"
        }
        else{
            nama_pelanggan = hutang.nama_pelanggan!!
        }
        binding.tvNamaPelanggan.setText(nama_pelanggan)

        if (hutang.hutang_type.equals("saya")){
            binding.etDeskripsi.setText(hutang.deskripsi)
            binding.etDeskripsi.visibility = View.VISIBLE
        }

        if (hutang.status.equals("lunas")){
            binding.rbSudahLunas.isChecked = true
            binding.btnSimpan.visibility = View.GONE
            binding.etBayarHutang.visibility = View.GONE
            binding.tvPembayaranCicilan.visibility = View.GONE
        }else{
            binding.rbBelumLunas.isChecked = true
        }
    }

    fun checkValidation(){
        val deskripsi =  binding.etDeskripsi.text.toString()
        val jumlahCicilan = binding.etBayarHutang.text.toString()

        if (hutang.hutang_type.equals("saya") && deskripsi.equals("")){
            showErrorMessage("Anda belum mengisi deskripsi")
        }else if (jumlahCicilan.isNullOrBlank()){
            showErrorMessage("Jumlah Pembayaran cicilan belum diisi")
        }
        else{
            hutang.deskripsi = deskripsi
            //updateHutang()
            bayarCiCilanHutang(jumlahCicilan)
        }
    }

    fun bayarCiCilanHutang(jumlahCicilan : String){
        showLoading(this)

        val cicilanDibayar = jumlahCicilan.toInt()
        val cicilanHutangInfo = CicilanHutangInfo(
            hutang.id!!,
            cicilanDibayar
        )

        ApiMain().services.saveCicilanHutang(mUserPref.getToken(),cicilanHutangInfo).enqueue(
            object : Callback<CommonResponse> {
                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    showErrorMessage("gagal melakukan perubahan data, coba lagi nanti")
                    Log.d(TAG_UPDATE_HUTANG,t.message.toString())
                    dismissLoading()
                }
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    val apiResponse = response.body()
                    val responseInfo = response.code()
                    Log.d(TAG_UPDATE_HUTANG,"body "+apiResponse!!.toString())
                    Log.d(TAG_UPDATE_HUTANG,"code "+responseInfo.toString())

                    dismissLoading()
                    if(response.code() == 200) {
                        showSuccessMessage(apiResponse.message)
                        onBackPressed()

                    }else if (response.code() == 202){
                        showErrorMessage(apiResponse.message)
                    }else if (response.code() == 401){
                        showErrorMessage("terjadi error pada token, login kembali..")
                        logout()
                        val i = Intent(this@DetailHutangActivity, SplashActivity::class.java)
                        startActivity(i)
                    }
                }
            }
        )
    }

    fun getDataCicilan(){
        showLoadingShimmer()

        ApiMain().services.getDataCicilan(mUserPref.getToken(),hutang.id).enqueue(object :
            retrofit2.Callback<CicilanHutangResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<CicilanHutangResponse>, response: Response<CicilanHutangResponse>) {
                //Tulis code jika response sukses
                Log.d(TAG_GET_CICILAN,response.toString())
                Log.d(TAG_GET_CICILAN,"http status : "+response.code())

                if(response.code() == 200) {
                    listCicilanHutang.clear()
                    response.body()?.data_cicilan?.let {
                        Log.d(TAG_GET_CICILAN,"dari API : "+it)
                        Log.d(TAG_GET_CICILAN,"jumlah dari API : "+it.size)
                        listCicilanHutang.addAll(it)
                        adapter.notifyDataSetChanged()

                        hideLoadingShimmer()
                        Log.d(TAG_GET_CICILAN,"isi adapter  : "+adapter.itemCount)
                    }

                    if (listCicilanHutang.size == 0){
                        binding.tvInfoEmpty.visibility = View.VISIBLE
                    }else{
                        countSisaCicilan()
                    }

                }else {
                    Toasty.error(this@DetailHutangActivity, "gagal mengambil data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_CICILAN,"err :"+response.message())
                }
            }
            override fun onFailure(call: Call<CicilanHutangResponse>, t: Throwable){
                //Tulis code jika response fail
                val errMsg = t.message.toString()
                if (errMsg.takeLast(6).equals("$.null")){
                    Log.d(TAG_GET_CICILAN,"rusak nya gpapa kok  ")
                    hideLoadingShimmer()
                }else{
                    Toasty.error(this@DetailHutangActivity, "response failure for more data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_GET_CICILAN,"rusak : "+t.message.toString())
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun countSisaCicilan(){
        var jmlCicilan = 0
        for (i in 0 until listCicilanHutang.size){
            jmlCicilan += listCicilanHutang.get(i).jml_cicilan!!
        }

        val sisaHutang = hutang.hutang?.minus(jmlCicilan)
        binding.tvInfoCicilan.setText("Riwayat Cicilan | Sisa Hutang : Rp. "+df.format(sisaHutang))
    }

    fun showLoadingShimmer(){
        binding.shimmer.sflMain.visibility = View.VISIBLE
        binding.shimmer.sflMain.startShimmerAnimation()

        binding.rvCicilan.visibility = View.GONE
        binding.tvInfoEmpty.visibility = View.GONE
    }

    fun hideLoadingShimmer(){
        if (binding.shimmer.sflMain.isVisible){
            binding.shimmer.sflMain.stopShimmerAnimation()
            binding.shimmer.sflMain.clearAnimation()
            binding.shimmer.sflMain.visibility = View.GONE
        }

        binding.rvCicilan.visibility = View.VISIBLE
    }

    fun updateHutang(){
        showLoading(this)

        val builder =
            MultipartBody.Builder().setType(MultipartBody.FORM)
        builder.addFormDataPart("id_hutang",hutang.id!!)
        builder.addFormDataPart("deskripsi",hutang.deskripsi!!)
        builder.addFormDataPart("status",hutang.status!!)
        val requestBody: RequestBody = builder.build()

        ApiMain().services.editHutang(mUserPref.getToken(),requestBody).enqueue(
            object : Callback<CommonResponse> {
                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    showErrorMessage("gagal melakukan simpan data, coba lagi nanti")
                    Log.d(TAG_UPDATE_HUTANG,t.message.toString())
                    dismissLoading()
                }
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    val apiResponse = response.body()
                    val responseInfo = response.code()
                    Log.d(TAG_UPDATE_HUTANG,"body "+apiResponse!!.toString())
                    Log.d(TAG_UPDATE_HUTANG,"code "+responseInfo.toString())

                    dismissLoading()
                    if(response.code() == 200) {
                        showSuccessMessage(apiResponse.message)
                        onBackPressed()

                    }else if (response.code() == 202){
                        showErrorMessage(apiResponse.message)
                    }else if (response.code() == 401){
                        showErrorMessage("terjadi error pada token, login kembali..")
                        logout()
                        val i = Intent(this@DetailHutangActivity, SplashActivity::class.java)
                        startActivity(i)
                    }
                }
            }
        )
    }
}