package com.example.kleineyt.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kleineyt.data.CartProduct
import com.example.kleineyt.firebase.FirebaseCommon
import com.example.kleineyt.helper.getProductPrice
import com.example.kleineyt.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore:FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon) : ViewModel() {

    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    private val _deleteDialog = MutableLiveData<CartProduct?>()
    val deleteDialog: LiveData<CartProduct?> = _deleteDialog

    val productsPrice = cartProducts.map{
        when(it){
            is Resource.Success ->{
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }


    private var cartProductsDocuments = emptyList<DocumentSnapshot>()

    init {
        getCartProducts()
    }

    private fun getCartProducts() {
        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener { value, error ->

                if (error != null || value == null) {
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }

                } else {
                    cartProductsDocuments = value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProducts)) }
                }

            }

    }



    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ) {
        val index = cartProducts.value.data?.indexOf(cartProduct)

        if (index != null && index != -1) {
            val documentId = cartProductsDocuments[index].id
            when (quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE ->
                {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading())}
                    increaseQuantity(documentId)
                }

                FirebaseCommon.QuantityChanging.DECREASE ->   {
                    if(cartProduct.quantity== 1){
                        Log.i("CartViewModel", "emit")
                        viewModelScope.launch { _deleteDialog.value = cartProduct }
                    }else{
                        viewModelScope.launch { _cartProducts.emit(Resource.Loading())}
                        decreaseQuantity(documentId)
                    }

                }
            }
        }

    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { result, error ->
            if (error != null) {
                viewModelScope.launch { _cartProducts.emit(Resource.Error(error.message.toString())) }
            }
        }

    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { result, error ->
            if (error != null) {
                viewModelScope.launch { _cartProducts.emit(Resource.Error(error.message.toString())) }
            }
        }
    }

    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()
    }

    fun deleteProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductsDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart").document(documentId).delete()
        }
    }

    fun makeDeleteDialogNull(){
         _deleteDialog.value = null
    }
}
