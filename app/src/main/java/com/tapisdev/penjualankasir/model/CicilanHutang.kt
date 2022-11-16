package com.tapisdev.penjualankasir.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class CicilanHutang(
    var id: String? = "",
    var id_hutang: String? = "",
    var jml_cicilan: Int? = 0,
    var status: String? = "",
    var tanggal: String? = "",

) : Parcelable