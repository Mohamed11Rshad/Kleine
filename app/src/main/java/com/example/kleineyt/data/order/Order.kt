package com.example.kleineyt.data.order

import android.os.Parcelable
import com.example.kleineyt.data.Address
import com.example.kleineyt.data.CartProduct
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus : String,
    val totalPrice : Float,
    val products : List<CartProduct>,
    val address : Address,
    val date : String = SimpleDateFormat("dd/MM/yyyy" , Locale.ENGLISH).format(Date()),
    val orderId : Long = nextLong(0, 1000000000000000000) + totalPrice.toLong()
) : Parcelable
{
    constructor() : this("", 0f,listOf(), Address())
}
