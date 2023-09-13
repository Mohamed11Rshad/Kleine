package com.example.kleineyt.util

import java.text.DecimalFormat


fun Float.formatPrice(): String {
    val decimalFormat = DecimalFormat("#,##0")
    return "$" + decimalFormat.format(this)
}