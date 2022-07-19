package com.tapisdev.penjualankasir.util


import com.tapisdev.penjualankasir.model.*
import com.tapisdev.penjualankasir.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {

    @POST("login")
    fun loginUser(@Body userData: LoginInfo) : Call<LoginResponse>

    @GET("logout")
    fun logoutUser(@Query("token") token : String) : Call<CommonResponse>

    @GET("reset-password")
    fun resetPassword(@Query("email") email : String) : Call<CommonResponse>

    @POST("register")
    fun registerUser(@Body userData: RegisterInfo) : Call<CommonResponse>

    @GET("user/detail")
    fun detailUser(@Query("token") token : String) : Call<DetailUserResponse>

    @POST("user/update")
    fun editProfil(@Query("token") token: String?,@Body file : RequestBody) : Call<CommonResponse>

    @POST("barang/add")
    fun addBarang(@Query("token") token: String?,@Body file : RequestBody) : Call<CommonResponse>

    @POST("barang/update")
    fun editBarang(@Query("token") token: String?,@Body file : RequestBody) : Call<CommonResponse>

    @GET("barang/data")
    fun getBarang(@Query("token") token: String?,@Query("page") page : Int, @Query("search") search : String) : Call<BarangResponse>

    @GET("barang/data/all")
    fun getAllBarang(@Query("token") token: String?) : Call<AllBarangResponse>

    @GET("barang/data/stok-tipis")
    fun getBarangStokTipis(@Query("token") token: String?,@Query("page") page : Int, @Query("search") search : String) : Call<BarangResponse>

    @GET("barang/delete")
    fun deleteBarang(@Query("token") token: String?,@Query("id_barang") id_barang : String) : Call<CommonResponse>

    @POST("pelanggan/add")
    fun savePelanggan(@Query("token") token: String?,@Body userData: PelangganInfo) : Call<CommonResponse>

    @GET("pelanggan/data")
    fun getPelanggan(@Query("token") token: String?,@Query("page") page : Int, @Query("search") search : String) : Call<PelangganResponse>

    @GET("pelanggan/data/all")
    fun getAllPelanggan(@Query("token") token: String?) : Call<AllPelangganResponse>

    @GET("pelanggan/delete")
    fun deletePelanggan(@Query("token") token: String?,@Query("id_pelanggan") id_pelanggan : String) : Call<CommonResponse>

    @POST("transaksi/store")
    fun saveTransaksi(@Query("token") token: String?,@Body orderInfo: OrderInfo) : Call<CommonResponse>

    @GET("transaksi/history")
    fun historyTransaksi(@Query("token") token: String?,@Query("page") page : Int) : Call<UntungResponse>

    @GET("transaksi/total-untung")
    fun getTotalUntung(@Query("token") token: String?) : Call<TotalUntungResponse>

    @GET("transaksi/detail")
    fun getHistoryTransaksi(@Query("token") token: String?,@Query("transaksi_id") transaksi_id: String?) : Call<HistoryTransaksiResponse>

    @GET("transaksi/chart")
    fun getDataChart(@Query("token") token: String?,@Query("selected_month") selected_month: String?) : Call<ChartTransaksiResponse>

    @GET("transaksi/report")
    fun getReportUntung(@Query("token") token: String?,@Query("dari") dari: String?,@Query("sampai") sampai: String?) : Call<UntungResponse>

    @GET("hutang/data")
    fun getDataHutang(@Query("token") token: String?,@Query("page") page : Int,@Query("hutang_type") hutang_type: String?,) : Call<HutangResponse>

    @POST("hutang/add")
    fun saveHutang(@Query("token") token: String?,@Body hutangInfo: HutangInfo) : Call<CommonResponse>

    @POST("hutang/update")
    fun editHutang(@Query("token") token: String?,@Body file : RequestBody) : Call<CommonResponse>

    @GET("hutang/report")
    fun getReportHutang(@Query("token") token: String?,@Query("dari") dari: String?,@Query("sampai") sampai: String?) : Call<HutangResponse>



}


