package com.dilarakiraz.upschoolcapstoneproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private var _mainState = MutableLiveData<HomeState>()
    val mainState: LiveData<HomeState>
        get() = _mainState

    private var _categoryList = MutableLiveData<List<String>>()
    val categoryList: LiveData<List<String>>
        get() = _categoryList

    private var _productsByCategory = MutableLiveData<List<ProductUI>>()
    val productsByCategory: LiveData<List<ProductUI>>
        get() = _productsByCategory

    private val _cartProductsCount = MutableLiveData<Int>()
    val cartProductsCount: LiveData<Int>
        get() = _cartProductsCount

    init {
        getProducts()
        getCategories()
    }

    private fun getProducts() = viewModelScope.launch {
        _mainState.value = HomeState.Loading
        val saleProducts = async { productRepository.getSaleProducts() }.await()
        val allProducts = async { productRepository.getAllProducts() }.await()

        _mainState.value = when {
            allProducts is Resource.Error -> HomeState.Error(allProducts.throwable)
            allProducts is Resource.Fail -> HomeState.EmptyScreen(allProducts.message)
            saleProducts is Resource.Error -> HomeState.Error(saleProducts.throwable)
            saleProducts is Resource.Fail -> HomeState.EmptyScreen(saleProducts.message)

            else -> {
                val product = (allProducts as Resource.Success)
                val sale = (saleProducts as Resource.Success)
                HomeState.Success(product.data, sale.data)
            }
        }
    }

    fun getProductsByCategory(category: String) = viewModelScope.launch {
        val productsByCategoryResource = productRepository.getProductsByCategory(category)
        if (productsByCategoryResource is Resource.Success) {
            val products = productsByCategoryResource.data
            _productsByCategory.value = products
        }
    }

    fun getCategories() = viewModelScope.launch {
        val categoriesResource = productRepository.getCategories()
        if (categoriesResource is Resource.Success) {
            val categories = categoriesResource.data
            _categoryList.value = categories
        }
    }

    fun setFavoriteState(product: ProductUI) = viewModelScope.launch {
        if (product.isFavorite) productRepository.deleteFromFavorites(product)
        else productRepository.addToFavorites(product)
        getProducts()
    }

    fun loadUserNickname(): MutableLiveData<String?> {
        val user = FirebaseAuth.getInstance().currentUser
        val nicknameLiveData = MutableLiveData<String?>()

        if (user != null) {
            val db = Firebase.firestore
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    val nickName = document.getString("nickname")
                    if (nickName != null) {
                        nicknameLiveData.value = nickName
                    }
                }
                .addOnFailureListener {}
        }
        return nicknameLiveData
    }

    fun fetchCartProductsCount() {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val cartProducts = productRepository.getCartProducts(userId)
                if (cartProducts is Resource.Success) {
                    val count = cartProducts.data.size
                    _cartProductsCount.value = count
                } else {
                    _cartProductsCount.value = 0
                }
            }
        }
    }
}

sealed interface HomeState {
    object Loading : HomeState
    data class EmptyScreen(val message: String) : HomeState
    data class Error(val throwable: Throwable) : HomeState
    data class Success(val saleProducts: List<ProductUI>, val products: List<ProductUI>) : HomeState
}