package com.tapisdev.penjualankasir.response

import com.google.gson.annotations.SerializedName
import com.tapisdev.penjualankasir.model.Barang
import com.tapisdev.penjualankasir.model.CicilanHutang

data class CicilanHutangResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data_cicilan : ArrayList<CicilanHutang>

)