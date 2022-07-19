package com.tapisdev.penjualankasir.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.penjualankasir.R
import com.tapisdev.penjualankasir.databinding.ActivityLupaPasswordBinding
import com.tapisdev.penjualankasir.response.CommonResponse
import com.tapisdev.penjualankasir.util.ApiMain
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LupaPasswordActivity : BaseActivity() {
    lateinit var binding  : ActivityLupaPasswordBinding
    var TAG_RESET = "resetPass"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLupaPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnReset.setOnClickListener {
            val email = binding.etEmail.text.toString()

            if (email.isNullOrBlank()){
                showErrorMessage("Email harus diisi")
            }else {
                sendResetPassword(email)
            }
        }
    }

    fun sendResetPassword(email : String){
        showLoading(this)
        ApiMain().services.resetPassword(email).enqueue(
            object : Callback<CommonResponse> {
                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    dismissLoading()
                    showInfoMessage("gagal reset password")
                    Log.d(TAG_RESET,t.message.toString())
                }
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    val responAPI = response.body()
                    val responseStatus = response.code()
                    dismissLoading()

                    Log.d(TAG_RESET,"status "+responseStatus.toString())

                    if(response.code() == 200) {
                        Log.d(TAG_RESET,"body "+responAPI!!.toString())
                        Log.d(TAG_RESET,"http code asli "+responseStatus.toString())
                        Log.d(TAG_RESET,"http code dari API "+responAPI!!.http_status)
                        showLongSuccessMessage(""+responAPI!!.message)

                        binding.etEmail.setText("")
                    }
                    else{
                        Log.d(TAG_RESET,"reset password gagal ")
                        showLongErrorMessage(""+responAPI!!.message)
                    }
                }
            }
        )
    }
}