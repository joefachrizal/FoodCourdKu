package com.cnd.foodcordku.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataTemp(
    var namaPesanan: String? = null,
    var jumlahPesanan: String? = null,
    var hargaPesanan: String? = null,

) : Parcelable