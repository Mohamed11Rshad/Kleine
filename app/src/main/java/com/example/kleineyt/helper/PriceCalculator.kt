package com.example.kleineyt.helper

fun Float?.getProductPrice(price:Float) : Float{
    if (this == null)
        return price

    else
        return ((1f - this) * price)

}