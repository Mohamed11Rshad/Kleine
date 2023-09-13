package com.example.kleineyt.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kleineyt.data.User
import com.example.kleineyt.util.Constants
import com.example.kleineyt.util.RegisterFieldState
import com.example.kleineyt.util.RegisterValidation
import com.example.kleineyt.util.Resource
import com.example.kleineyt.util.validateEmail
import com.example.kleineyt.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db : FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register : Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User , password : String){
        if(checkValidation(user , password))
        {
            runBlocking { _register.emit(Resource.Loading()) }
            firebaseAuth.createUserWithEmailAndPassword(user.email , password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid , user)
                    }
                }
                .addOnFailureListener{
                    _register.value =  Resource.Error(it.message.toString())

                }
        }
        else
        {
            val registerFieldState = RegisterFieldState(
                validateEmail(user.email),
                validatePassword(password)
            )
            runBlocking { _validation.send(registerFieldState) }
        }

    }

    private fun saveUserInfo(userUid : String , user: User) {
        db.collection(Constants.USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener { _register.value = Resource.Success(user) }
            .addOnFailureListener { _register.value = Resource.Error(it.message.toString()) }

    }

    fun checkValidation(user: User , password : String) : Boolean{
        return (validateEmail(user.email) is RegisterValidation.Success
                && validatePassword(password) is RegisterValidation.Success)
    }
}