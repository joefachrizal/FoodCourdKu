package com.cnd.foodcordku.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataPemesan(
    var statusPesanan: String? = null,
    var pemesan: String? = null,
    var hargaBayar: String? = null,
    var banyakPesanan: String? = null,
    var iduser: String? = null,
    var waktu: String? = null,
    var Tanggal: String? = null,
    var jam: String? = null,
    var meja: String? = null,
    var takeAway: String? = null,
    var statusBayar: String? = null,
) : Parcelable