package com.tapisdev.penjualankasir.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.penjualankasir.R
import com.tapisdev.penjualankasir.databinding.ActivityUbahProfileBinding
import com.tapisdev.penjualankasir.model.SharedVariable
import com.tapisdev.penjualankasir.model.UserPreference
import com.tapisdev.penjualankasir.response.CommonResponse
import com.tapisdev.penjualankasir.util.ApiMain
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UbahProfileActivity : BaseActivity() {
    lateinit var binding : ActivityUbahProfileBinding
    var TAG_UPDATE_USER = "updateUser"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mUserPref = UserPreference(this)

        binding.btnUpdate.setOnClickListener {
            checkValidation()
        }
        binding.btnUpdatePassword.setOnClickListener {
            checkValidationPassword()
        }
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        updateUI()
    }

    fun updateUI(){
        binding.etPhone.setText(mUserPref.getPhone())
        binding.etAlamat.setText(mUserPref.getAlamat())
        binding.etNamaPemilik.setText(mUserPref.getName())
        binding.etNamaUmkm.setText(mUserPref.getNamaUmkm())
    }

    fun checkValidation(){
        val phone = binding.etPhone.text.toString()
        val alamat  = binding.etAlamat.text.toString()
        val pemilik  = binding.etNamaPemilik.text.toString()
        val namaUmkm  = binding.etNamaUmkm.text.toString()

        if (phone.isNullOrBlank()){
            showErrorMessage("Nomor telepon harus diisi")
        }else if (alamat.isNullOrBlank()){
            showErrorMessage("alamat harus diisi")
        }else if (pemilik.isNullOrBlank()){
            showErrorMessage("Nama pemilik harus diisi")
        }else if (namaUmkm.isNullOrBlank()){
            showErrorMessage("Nama UMKM harus diisi")
        }else{
            updateProfile(phone,alamat,pemilik,namaUmkm)
        }
    }

    fun checkValidationPassword(){
        val password = binding.etPassword.text.toString()
        val confirm_password = binding.etCPassword.text.toString()

        if (password.isNullOrBlank()){
            showErrorMessage("Password baru belum diisi")
        }else if (confirm_password.isNullOrBlank()){
            showErrorMessage("Konfirmasi Password baru belum diisi")
        }else if (!password.equals(confirm_password)){
            showErrorMessage("Konfirmasi password tidak valid")
        }else {
            updatePassword(password)
        }
    }

    fun updateSession(phone : String,
                      alamat : String,
                      pemilik : String,
                      nama_umkm : String){
        mUserPref.savePhone(phone)
        mUserPref.saveAlamat(alamat)
        mUserPref.saveName(pemilik)
        mUserPref.saveNamaUmkm(nama_umkm)
    }

    fun updateProfile(
        phone : String,
        alamat : String,
        pemilik : String,
        nama_umkm : String
    ){
        showLoading(this)

        val builder =
            MultipartBody.Builder().setType(MultipartBody.FORM)
        builder.addFormDataPart("phone",phone)
        builder.addFormDataPart("alamat",alamat)
        builder.addFormDataPart("name",pemilik)
        builder.addFormDataPart("nama_umkm",nama_umkm)
        val requestBody: RequestBody = builder.build()

        ApiMain().services.editProfil(mUserPref.getToken(),requestBody).enqueue(
            object : Callback<CommonResponse> {
                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    showErrorMessage("gagal melakukan simpan data, coba lagi nanti")
                    Log.d(TAG_UPDATE_USER,t.message.toString())
                    dismissLoading()
                }
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    val apiResponse = response.body()
                    val responseInfo = response.code()
                    Log.d(TAG_UPDATE_USER,"body "+apiResponse!!.toString())
                    Log.d(TAG_UPDATE_USER,"code "+responseInfo.toString())

                    dismissLoading()
                    if(response.code() == 200) {
                        showSuccessMessage(apiResponse.message)
                        updateSession(phone, alamat, pemilik, nama_umkm)

                        SharedVariable.nextFragment = ""
                        val i = Intent(this@UbahProfileActivity, HomeActivity::class.java)
                        startActivity(i)
                        finish()
                    }else if (response.code() == 202){
                        showErrorMessage(apiResponse.message)
                    }else if (response.code() == 401){
                        showErrorMessage("terjadi error pada token, login kembali..")
                        logout()
                        val i = Intent(this@UbahProfileActivity, SplashActivity::class.java)
                        startActivity(i)
                    }
                }
            }
        )

    }

    fun updatePassword(password : String){
        showLoading(this)

        val builder =
            MultipartBody.Builder().setType(MultipartBody.FORM)
        builder.addFormDataPart("password",password)
        val requestBody: RequestBody = builder.build()

        ApiMain().services.editProfil(mUserPref.getToken(),requestBody).enqueue(
            object : Callback<CommonResponse> {
                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    showErrorMessage("gagal melakukan simpan data, coba lagi nanti")
                    Log.d(TAG_UPDATE_USER,t.message.toString())
                    dismissLoading()
                }
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    val apiResponse = response.body()
                    val responseInfo = response.code()
                    Log.d(TAG_UPDATE_USER,"body "+apiResponse!!.toString())
                    Log.d(TAG_UPDATE_USER,"code "+responseInfo.toString())

                    dismissLoading()
                    if(response.code() == 200) {
                        showSuccessMessage(apiResponse.message)

                        SharedVariable.nextFragment = ""
                        val i = Intent(this@UbahProfileActivity, HomeActivity::class.java)
                        startActivity(i)
                        finish()
                    }else if (response.code() == 202){
                        showErrorMessage(apiResponse.message)
                    }else if (response.code() == 401){
                        showErrorMessage("terjadi error pada token, login kembali..")
                        logout()
                        val i = Intent(this@UbahProfileActivity, SplashActivity::class.java)
                        startActivity(i)
                    }
                }
            }
        )
    }
}