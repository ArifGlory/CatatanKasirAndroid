package com.tapisdev.penjualankasir.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.penjualankasir.MainActivity
import com.tapisdev.penjualankasir.R
import com.tapisdev.penjualankasir.adapter.AdapterPelanggan
import com.tapisdev.penjualankasir.databinding.ActivityHomeBinding
import com.tapisdev.penjualankasir.fragment.*
import com.tapisdev.penjualankasir.model.Pelanggan
import com.tapisdev.penjualankasir.model.SharedVariable
import com.tapisdev.penjualankasir.model.UserPreference
import com.tapisdev.penjualankasir.response.AllPelangganResponse
import com.tapisdev.penjualankasir.response.CommonResponse
import com.tapisdev.penjualankasir.util.ApiMain
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Response


class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    lateinit var nextFragment  : Fragment
    var TAG_DELETE_PELANGGAN = "deletepelanggan"
    var listener: DialogInterface.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUserPref = UserPreference(this)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (SharedVariable.nextFragment.equals("")){
            nextFragment = HomeFragment.newInstance()
        }else if (SharedVariable.nextFragment.equals("stok")){
            nextFragment = StokFragment.newInstance()
        }else if (SharedVariable.nextFragment.equals("hutang")){
            nextFragment = HutangFragment.newInstance()
        }else if (SharedVariable.nextFragment.equals("untung")){
            nextFragment = UntungFragment.newInstance()
        }else if (SharedVariable.nextFragment.equals("transaksi")){
            nextFragment = TransaksiFragment.newInstance()
        }

        addFragment(nextFragment)
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val fragment = HomeFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_stok -> {
                val fragment = StokFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_hutang -> {
                val fragment = HutangFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_untung -> {
                val fragment = UntungFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_transaksi -> {
                val fragment = TransaksiFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }


        }
        false
    }


    override fun onResume() {
        super.onResume()
    }

    fun deletePelanggan(id_pelanggan : String){
        showLoading(this)

        ApiMain().services.deletePelanggan(mUserPref.getToken(),id_pelanggan).enqueue(object :
            retrofit2.Callback<CommonResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                dismissLoading()
                //Tulis code jika response sukses
                Log.d(TAG_DELETE_PELANGGAN,response.toString())
                Log.d(TAG_DELETE_PELANGGAN,"http status : "+response.code())

                if(response.code() == 200) {
                    showSuccessMessage("Hapus pelanggan berhasil !")

                    finish()
                    startActivity(intent)
                }else {
                    Toasty.error(this@HomeActivity, "hapus pelanggan gagal", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_DELETE_PELANGGAN,"err :"+response.message())
                }
            }
            override fun onFailure(call: Call<CommonResponse>, t: Throwable){
                //Tulis code jika response fail
                dismissLoading()
                val errMsg = t.message.toString()
                if (errMsg.takeLast(6).equals("$.null")){
                    Log.d(TAG_DELETE_PELANGGAN,"rusak nya gpapa kok  ")
                }else{
                    Toasty.error(this@HomeActivity, "response failure for more data", Toast.LENGTH_SHORT, true).show()
                    Log.d(TAG_DELETE_PELANGGAN,"rusak : "+t.message.toString())
                }
            }
        })
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
            .replace(R.id.content, fragment, fragment.javaClass.getSimpleName())
            .commit()
    }

    override fun onBackPressed() {
       // super.onBackPressed()
        val builder =
            AlertDialog.Builder(this)
        builder.setMessage("Apakan anda ingin keluar dari aplikasi ? ")
        builder.setCancelable(false)
        val i = Intent(this,MainActivity::class.java)

        listener = DialogInterface.OnClickListener { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                startActivity(i)
            }
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                dialog.cancel()
            }
        }
        builder.setPositiveButton("Ya", listener)
        builder.setNegativeButton("Tidak", listener)
        builder.show()
    }
}