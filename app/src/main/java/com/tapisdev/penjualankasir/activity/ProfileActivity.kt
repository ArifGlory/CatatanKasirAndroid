package com.tapisdev.penjualankasir.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.penjualankasir.MainActivity
import com.tapisdev.penjualankasir.R
import com.tapisdev.penjualankasir.databinding.ActivityProfileBinding
import com.tapisdev.penjualankasir.model.UserPreference
import com.tapisdev.penjualankasir.response.CommonResponse
import com.tapisdev.penjualankasir.util.ApiMain
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : BaseActivity() {
    lateinit var binding : ActivityProfileBinding
    var TAG_LOGOUT = "logout"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mUserPref = UserPreference(this)

        binding.lineLogout.setOnClickListener {
            sendLogoutRequest()
            logout()
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
        }
        binding.tvEditProfile.setOnClickListener {
            val i = Intent(this,UbahProfileActivity::class.java)
            startActivity(i)
        }

        updateUI()
    }

    fun updateUI(){
        val firstChara = mUserPref.getName()?.take(1)
        binding.tvInisial.setText(firstChara)
        binding.tvName.setText(mUserPref.getName())
        binding.tvEmail.setText(mUserPref.getEmail())
    }

    fun sendLogoutRequest(){
        ApiMain().services.logoutUser(mUserPref.getToken()!!).enqueue(
            object : Callback<CommonResponse> {
                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    showInfoMessage("gagal logout user")
                    Log.d(TAG_LOGOUT,t.message.toString())
                }
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    val responAPI = response.body()
                    val responseStatus = response.code()

                    Log.d(TAG_LOGOUT,"status "+responseStatus.toString())

                    if(response.code() == 200) {
                        Log.d(TAG_LOGOUT,"body "+responAPI!!.toString())
                        Log.d(TAG_LOGOUT,"http code asli "+responseStatus.toString())
                        Log.d(TAG_LOGOUT,"http code dari API "+responAPI!!.http_status)
                        Log.d(TAG_LOGOUT,"Logout request berhasil ")

                    }
                    else{
                        Log.d(TAG_LOGOUT,"Logout request gagal ")
                    }
                }
            }
        )
    }
}