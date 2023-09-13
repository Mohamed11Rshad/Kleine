package com.example.kleineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kleineyt.data.Category
import com.example.kleineyt.data.Product
import com.example.kleineyt.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {

    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    private val pagingInfo = BaseCategoryPagingInfo()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts() {
        if (!pagingInfo.isOffersPagingEnd) {

            viewModelScope.launch {
                _offerProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").whereEqualTo("category", category.category)
                .whereNotEqualTo("offerPercentage", null)
                .limit(pagingInfo.offersPage * 5)
                .get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)

                    pagingInfo.isOffersPagingEnd = pagingInfo.offersOldList == products
                    pagingInfo.offersOldList = products

                    viewModelScope.launch {
                        _offerProducts.emit(Resource.Success(products))
                    }

                    pagingInfo.offersPage++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _offerProducts.emit(Resource.Error(it.message.toString()))
                    }

                }
        }
    }

    fun fetchBestProducts() {

        if(!pagingInfo.isBestProductsPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").whereEqualTo("category", category.category)
                .whereEqualTo("offerPercentage", null)
                .limit(pagingInfo.bestProductsPage * 10)
                .get()
                .addOnSuccessListener {
                    val products = it.toObjects(Product::class.java)

                    pagingInfo.isBestProductsPagingEnd = pagingInfo.bestProductOldList == products
                    pagingInfo.bestProductOldList = products
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(products))
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
}




    internal data class BaseCategoryPagingInfo(
        var offersPage: Long = 1,
        var bestProductsPage: Long = 1,
        var bestProductOldList : List<Product> = emptyList(),
        var offersOldList : List<Product> = emptyList(),
        var isBestProductsPagingEnd : Boolean = false,
        var isOffersPagingEnd : Boolean = false,

    )