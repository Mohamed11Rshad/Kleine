package com.example.kleineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kleineyt.data.User
import com.example.kleineyt.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {


    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    fun getUser(){

        viewModelScope.launch { _user.emit(Resource.Loading()) }

        firestore.collection("user").document(auth.uid!!).addSnapshotListener {
                value, error ->

            if (error!=null){
                viewModelScope.launch { _user.emit(Resource.Error(error.message!!)) }
            }else{
                val user = value!!.toObject(User::class.java)
                viewModelScope.launch { _user.emit(Resource.Success(user!!)) }
            }


        }
    }

    fun logOut() {
        auth.signOut()
    }

}