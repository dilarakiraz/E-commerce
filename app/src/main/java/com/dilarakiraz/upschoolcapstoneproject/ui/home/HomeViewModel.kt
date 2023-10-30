package com.dilarakiraz.upschoolcapstoneproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    private val _user = MutableLiveData<String?>()
    val userNickname: LiveData<String?>
        get() = _user

    private val _cartProductsCount = MutableLiveData<Int>()
    val cartProductsCount: LiveData<Int>
        get() = _cartProductsCount

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    init {
        getProducts()
        getCategories()

        viewModelScope.launch {
            performUserOperations()
        }
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

            else -> HomeState.Success(
                (allProducts as Resource.Success).data,
                (saleProducts as Resource.Success).data,
            )
        }
    }

    fun getProductsByCategory(category: String) = viewModelScope.launch {
        val productsByCategoryResource = productRepository.getProductsByCategory(category)
        if (productsByCategoryResource is Resource.Success) {
            val products = productsByCategoryResource.data
            _productsByCategory.value = products
        }
    }

    private fun getCategories() = viewModelScope.launch {
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

//    private suspend fun performUserOperations() {
//        val userId = user?.uid.orEmpty()
//
//        val nickname = withContext(Dispatchers.IO) {
//            if (userId.isEmpty()) null
//            else {
//                try {
//                    val document = db.collection("users").document(userId).get().await()
//                    document.getString("nickname")
//                } catch (e: Exception) {
//                    null
//                }
//            }
//        }
//        _userNickname.value = nickname
//
//        val cartProductsResource = productRepository.getCartProducts(userId)
//        val count = if (cartProductsResource is Resource.Success) {
//            cartProductsResource.data.size
//        } else 0
//        _cartProductsCount.value = count
//    }

    private suspend fun performUserOperations() {
        val userId = user?.uid.orEmpty()

        val userDocument = getUserDocument(userId)

        val nickname = userDocument?.getString("nickname")
        _user.value = nickname

        val cartProductsResource = productRepository.getCartProducts(userId)
        val count = if (cartProductsResource is Resource.Success) {
            cartProductsResource.data.size
        } else 0
        _cartProductsCount.value = count

        val profileImageUrl = userDocument?.getString("profileImageUrl")
    }

    private suspend fun getUserDocument(userId: String): DocumentSnapshot? = withContext(Dispatchers.IO) {
        if (userId.isEmpty()) return@withContext null
        try {
            db.collection("users").document(userId).get().await()
        } catch (e: Exception) {
            null
        }
    }
}

sealed interface HomeState {
    object Loading : HomeState
    data class EmptyScreen(val message: String) : HomeState
    data class Error(val throwable: Throwable) : HomeState
    data class Success(val saleProducts: List<ProductUI>, val products: List<ProductUI>) : HomeState
}