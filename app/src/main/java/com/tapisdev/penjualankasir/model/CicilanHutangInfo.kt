package com.tapisdev.penjualankasir.model

import com.google.gson.annotations.SerializedName

data class CicilanHutangInfo (
    @SerializedName("id_hutang") val id_hutang: String?,
    @SerializedName("jml_cicilan") val jml_cicilan: Int?,
)