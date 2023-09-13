package com.example.kleineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kleineyt.data.Address
import com.example.kleineyt.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore : FirebaseFirestore,
    private val auth : FirebaseAuth
) : ViewModel(){

    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()

    private val _deleteAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val deleteAddress = _deleteAddress.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun AddAddress(address : Address){

        viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }

        val validateInputs = validateInputs(address)

        if (!validateInputs(address)){
                firestore.collection("user").document(auth.uid!!).collection("address")
                    .document(address.addressTitle)
                    .set(address).addOnSuccessListener {
                        viewModelScope.launch { _addNewAddress.emit(Resource.Success(address)) }

                    }.addOnFailureListener {
                        viewModelScope.launch { _addNewAddress.emit(Resource.Error(it.message.toString())) }

                    }
        }else{
            viewModelScope.launch { _error.emit("Please fill all the fields") }
        }

    }

    private fun validateInputs(address : Address): Boolean {

        return address.addressTitle.trim().isEmpty()||
                address.fullName.trim().isEmpty()||
                address.street.trim().isEmpty()||
                address.phone.trim().isEmpty()||
                address.city.trim().isEmpty()||
                address.state.trim().isEmpty()
    }

    fun deleteAddress(address: Address){

        viewModelScope.launch { _deleteAddress.emit(Resource.Loading()) }

        firestore.collection("user").document(auth.uid!!).collection("address")
            .document(address.addressTitle)
            .delete().addOnSuccessListener {
                viewModelScope.launch { _deleteAddress.emit(Resource.Success(address)) }
            }.addOnFailureListener {
                viewModelScope.launch { _deleteAddress.emit(Resource.Error(it.message.toString())) }
            }
    }

}