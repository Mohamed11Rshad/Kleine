package com.example.kleineyt.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kleineyt.R
import com.example.kleineyt.util.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) : ViewModel(){

    private val _navigate = MutableStateFlow(0)
    val navigate : StateFlow<Int> = _navigate

    companion object{
        const val SHOPPING_ACTIVITY = 25
        const val ACCOUNT_OPTION_FRAGMENT = 26
    }

    init {
        val isButtonClicked = sharedPreferences.getBoolean(Constants.INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser

        if (user!=null){
        viewModelScope.launch {
            _navigate.emit(SHOPPING_ACTIVITY)
        }

        }
        else if (isButtonClicked){
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTION_FRAGMENT)
            }
        }

        else{
            Unit
        }
    }

    fun startButtonClicked(){
        sharedPreferences.edit().putBoolean(Constants.INTRODUCTION_KEY, true).apply()
    }

}