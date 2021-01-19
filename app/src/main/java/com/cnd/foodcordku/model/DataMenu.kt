package com.cnd.foodcordku.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataMenu(
    var titleFood: String? = null,
    var price: String? = null,
    var description: String? = null,
    var stokMakanan: String? = null,
    var keyMakanan: String? = null,
    var otlet: String? = null,
    var urlImage: String? = null
) : Parcelable