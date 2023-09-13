package com.example.kleineyt.viewmodel

import android.graphics.pdf.PdfDocument.PageInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kleineyt.data.CartProduct
import com.example.kleineyt.data.Product
import com.example.kleineyt.firebase.FirebaseCommon
import com.example.kleineyt.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore : FirebaseFirestore,
    private val auth : FirebaseAuth,
    private val firebaseCommon: FirebaseCommon

) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts : StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts : StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts : StateFlow<Resource<List<Product>>> = _bestProducts

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart : StateFlow<Resource<CartProduct>> = _addToCart

    private val pagingInfo = PagingInfo()


    init {
        fetchSpecialProducts()
        fetchBestDealsProducts()
        fetchBestProducts()

    }

    fun fetchSpecialProducts() {

        if (!pagingInfo.isSpecialProductsPagingEnd) {

            viewModelScope.launch {
                _specialProducts.emit(Resource.Loading())
            }

            firestore
                .collection("Products")
                .whereEqualTo("category","Special Products")
                .limit(pagingInfo.specialProductsPage * 5)
                .get()

                .addOnSuccessListener { result ->
                    val specialProductsList = result.toObjects(Product::class.java)

                    pagingInfo.isSpecialProductsPagingEnd = pagingInfo.specialProductsOldList == specialProductsList
                    pagingInfo.specialProductsOldList = specialProductsList

                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(specialProductsList))
                    }

                    pagingInfo.specialProductsPage++
                }

                .addOnFailureListener {
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestDealsProducts(){

        if(!pagingInfo.isBestDealsPagingEnd){
        viewModelScope.launch {
            _bestDealsProducts.emit(Resource.Loading())
        }

        firestore
            .collection("Products")
            .limit(pagingInfo.bestDealsPage * 5)
            .whereEqualTo("category","Best deals")
            .get()

            .addOnSuccessListener {result ->
                val bestDealsProducts = result.toObjects(Product::class.java)

                pagingInfo.isBestDealsPagingEnd = pagingInfo.bestDealsOldList == bestDealsProducts
                pagingInfo.bestDealsOldList = bestDealsProducts

                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Success(bestDealsProducts))
                }

                pagingInfo.bestDealsPage++
            }

            .addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
        }

    fun fetchBestProducts() {

        if (!pagingInfo.isBestPagingEnd) {

            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }

            firestore.collection("Products").limit(pagingInfo.bestProductsPage * 10).get()

                .addOnSuccessListener { result ->
                    val bestProducts = result.toObjects(Product::class.java)

                    pagingInfo.isBestPagingEnd = pagingInfo.bestProductOldList == bestProducts
                    pagingInfo.bestProductOldList = bestProducts

                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProducts))
                    }
                    pagingInfo.bestProductsPage++
                }

                .addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun addToCart(cartProduct: CartProduct) {

        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id",cartProduct.product.id)
            .whereEqualTo("selectedColor",cartProduct.selectedColor)
            .whereEqualTo("selectedSize",cartProduct.selectedSize).get()
            .addOnSuccessListener {
                it.documents.let{
                    if(it.isEmpty()){//Add new product
                        addNewProduct(cartProduct)
                    }else{
                        val product = it.first().toObject(CartProduct::class.java)
                        if(product?.selectedColor == cartProduct.selectedColor
                            && product?.selectedSize == cartProduct.selectedSize
                            && product?.product == cartProduct.product){//Increase quantity
                            val documentId = it.first().id
                            increaseQuantity(documentId,cartProduct)
                        }
                        else{//Add new product
                            addNewProduct(cartProduct)
                        }

                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }

    }

    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){addedProduct,exception ->
            if(exception != null){
                viewModelScope.launch { _addToCart.emit(Resource.Error(exception.message.toString())) }
            }else{
                viewModelScope.launch { _addToCart.emit(Resource.Success(addedProduct!!)) }
            }
        }
    }

    private fun increaseQuantity(documentId:String , cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){updatedDocumentId,exception ->
            if(exception != null){
                viewModelScope.launch { _addToCart.emit(Resource.Error(exception.message.toString())) }
            }else{
                viewModelScope.launch { _addToCart.emit(Resource.Success(cartProduct)) }
            }
        }
    }

}

internal data class PagingInfo(
    var specialProductsPage: Long = 1,
    var bestDealsPage: Long = 1,
    var bestProductsPage: Long = 1,
    var bestProductOldList : List<Product> = emptyList(),
    var bestDealsOldList : List<Product> = emptyList(),
    var specialProductsOldList : List<Product> = emptyList(),
    var isBestPagingEnd : Boolean = false,
    var isBestDealsPagingEnd : Boolean = false,
    var isSpecialProductsPagingEnd : Boolean = false



)