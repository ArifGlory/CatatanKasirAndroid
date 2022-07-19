package com.tapisdev.penjualankasir

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.penjualankasir.activity.HomeActivity
import com.tapisdev.penjualankasir.activity.LupaPasswordActivity
import com.tapisdev.penjualankasir.activity.RegisterActivity
import com.tapisdev.penjualankasir.activity.SplashActivity
import com.tapisdev.penjualankasir.databinding.ActivityMainBinding
import com.tapisdev.penjualankasir.model.LoginInfo
import com.tapisdev.penjualankasir.model.UserPreference
import com.tapisdev.penjualankasir.response.LoginResponse
import com.tapisdev.penjualankasir.util.ApiMain
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.sign

class MainActivity : BaseActivity() {
    lateinit var binding  : ActivityMainBinding
    lateinit var loginInfo: LoginInfo
    var TAG_LOGIN = "login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mUserPref = UserPreference(this)

        binding.btnLogin.setOnClickListener {
            checkValidation()
        }
        binding.tvKeRegister.setOnClickListener {
            val i = Intent(this,RegisterActivity::class.java)
            startActivity(i)
        }
        binding.tvLupaPass.setOnClickListener {
            val i = Intent(this,LupaPasswordActivity::class.java)
            startActivity(i)
        }
    }

    fun checkValidation(){
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.equals("") || email.length == 0){
            showErrorMessage("Email Belum diisi")
        }else if (password.equals("") || password.length == 0){
            showErrorMessage("Password Belum diisi")
        }else {
            loginInfo = LoginInfo(email, password)
            signIn(loginInfo)
        }
    }

    fun signIn(loginInfo: LoginInfo){
        showLoading(this)
        Log.d(TAG_LOGIN,loginInfo.toString())

        ApiMain().services.loginUser(loginInfo).enqueue(
            object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    showErrorMessage("gagal melakukan login, coba lagi nanti")
                    Log.d(TAG_LOGIN,t.message.toString())
                    dismissLoading()
                }
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val responAPI = response.body()
                    val responseStatus = response.code()


                    dismissLoading()
                    if(response.code() == 200) {
                        Log.d(TAG_LOGIN,"body "+responAPI!!.toString())
                        Log.d(TAG_LOGIN,"http code asli "+responseStatus.toString())
                        Log.d(TAG_LOGIN,"token user "+responAPI!!.token)
                        Log.d(TAG_LOGIN,"http code dari API "+responAPI!!.http_status)
                        mUserPref.saveToken(responAPI!!.token)

                        val i = Intent(this@MainActivity, SplashActivity::class.java)
                        startActivity(i)

                    }else if (response.code() == 202){
                        showErrorMessage("Login gagal, cek kembali email dan password anda")
                    }
                }
            }
        )
    }
}