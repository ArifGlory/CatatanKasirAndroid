package com.tapisdev.penjualankasir.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.penjualankasir.MainActivity
import com.tapisdev.penjualankasir.R
import com.tapisdev.penjualankasir.databinding.ActivityRegisterBinding
import com.tapisdev.penjualankasir.model.RegisterInfo
import com.tapisdev.penjualankasir.response.CommonResponse
import com.tapisdev.penjualankasir.util.ApiMain
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : BaseActivity() {
    lateinit var binding : ActivityRegisterBinding
    lateinit var registerInfo : RegisterInfo
    var TAG_DAFTAR = "daftar"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnDaftar.setOnClickListener {
            checkValidation()
        }
    }

    fun checkValidation(){
        val name = binding.etNamaPemilik.text.toString()
        val phone = binding.etPhone.text.toString()
        val email = binding.etEmail.text.toString()
        val nama_umkm = binding.etNamaUmkm.text.toString()
        val alamat = binding.etAlamat.text.toString()
        val password = binding.etPassword.text.toString()
        val confirm_password = binding.etCPassword.text.toString()

        if (name.equals("") || name.length == 0){
            showErrorMessage("Nama Belum diisi")
        }else  if (phone.equals("") || phone.length == 0){
            showErrorMessage("Telepon Belum diisi")
        }else if (email.equals("") || email.length == 0){
            showErrorMessage("Email Belum diisi")
        }else if (nama_umkm.equals("") || nama_umkm.length == 0){
            showErrorMessage("Nama UMKM Belum diisi")
        }else if (alamat.equals("") || alamat.length == 0){
            showErrorMessage("Alamat Belum diisi")
        }else if (password.equals("") || password.length == 0){
            showErrorMessage("Password Belum diisi")
        }else if (confirm_password.equals("") || confirm_password.length == 0){
            showErrorMessage("Konfirmasi Password Belum diisi")
        }else if (!password.equals(confirm_password)){
            showErrorMessage("Konfirmasi password tidak valid")
        }else if (password.length < 6){
            showErrorMessage("password harus lebih dari 6 karakter")
        }
        else {
            registerInfo = RegisterInfo(email, password, name, nama_umkm, phone, alamat)
            sendRegistration(registerInfo)
        }
    }

    fun sendRegistration(
       registerInfo: RegisterInfo
    ){
        showLoading(this)
        Log.d(TAG_DAFTAR,registerInfo.toString())

        ApiMain().services.registerUser(registerInfo).enqueue(
            object : Callback<CommonResponse> {
                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    showErrorMessage("gagal melakukan pendaftaran, coba lagi nanti")
                    Log.d(TAG_DAFTAR,t.message.toString())
                    dismissLoading()
                }
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    val responAPI = response.body()
                    val responseStatus = response.code()


                    dismissLoading()
                    if(response.code() == 200) {
                        Log.d(TAG_DAFTAR,"body "+responAPI!!.toString())
                        Log.d(TAG_DAFTAR,"http code asli "+responseStatus.toString())
                        Log.d(TAG_DAFTAR,"http code dari API "+responAPI!!.http_status)

                        showSuccessMessage("Pendaftaran berhasil, silahkan login")

                        val i = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(i)

                    }else if (response.code() == 202){
                        showErrorMessage("Pendaftaran gagal, cek kembali koneksi anda")
                    }
                }
            }
        )
    }
}