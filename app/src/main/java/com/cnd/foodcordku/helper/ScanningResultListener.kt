package com.cnd.foodcordku.helper

import android.content.Context

interface ScanningResultListener {
    fun onScanned(result: String)
}