package com.example.kleineyt.data

sealed class Category(val category : String){
    object Chair : Category("Chair")
    object Table : Category("Table")
    object Cupboard : Category("Cupboard")
    object Accessory : Category("Accessory")
    object Furniture : Category("Furniture")
}
