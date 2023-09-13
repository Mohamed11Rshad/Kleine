package com.example.kleineyt.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.kleineyt.R
import com.example.kleineyt.activities.ShoppingActivity

fun Fragment.hideNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<View>(R.id.bottomNavigation)
    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<View>(R.id.bottomNavigation)
    bottomNavigationView.visibility = View.VISIBLE
}