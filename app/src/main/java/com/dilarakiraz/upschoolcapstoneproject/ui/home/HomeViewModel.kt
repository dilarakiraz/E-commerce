package com.dilarakiraz.upschoolcapstoneproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.UserData
import com.dilarakiraz.upschoolcapstoneproject.data.repository.ProductRepository
import com.dilarakiraz.upschoolcapstoneproject.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var _mainState = MutableLiveData<HomeState>()
    val mainState: LiveData<HomeState>
        get() = _mainState

    init {
        getProducts()
        getCategories()
        performUserOperations()
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
                (saleProducts as Resource.Success).data,
                (allProducts as Resource.Success).data,
            )
        }
    }

    fun getProductsByCategory(category: String) = viewModelScope.launch {
        _mainState.value = when (val productsByCategoryResource =
            productRepository.getProductsByCategory(category)) {
            is Resource.Success -> HomeState.ProductsByCategory(productsByCategoryResource.data)
            is Resource.Error -> HomeState.Error(productsByCategoryResource.throwable)
            is Resource.Fail -> HomeState.EmptyScreen("Failed to fetch products by category")
        }
    }

    private fun getCategories() = viewModelScope.launch {
        val categoriesResource = productRepository.getCategories()
        _mainState.value = when (categoriesResource) {
            is Resource.Success -> HomeState.Category(categoriesResource.data)
            is Resource.Error -> HomeState.Error(categoriesResource.throwable)
            is Resource.Fail -> HomeState.EmptyScreen("Failed to fetch categories")
        }
    }

    fun setFavoriteState(product: ProductUI) = viewModelScope.launch {
        if (product.isFavorite) productRepository.deleteFromFavorites(product)
        else productRepository.addToFavorites(product)
        getProducts()
    }

    private fun performUserOperations() = viewModelScope.launch {
        val userId = userRepository.getUserUid()

        when (val userResource = userRepository.getUserData(userId)) {
            is Resource.Success -> {
                val user = userResource.data
                _mainState.value = HomeState.User(user)
            }

            is Resource.Error -> {
                _mainState.value = HomeState.Error(userResource.throwable)
            }

            is Resource.Fail -> {
                _mainState.value = HomeState.EmptyScreen("Kullanıcı verileri alınamadı.")
            }
        }
    }
}

sealed interface HomeState {
    object Loading : HomeState
    data class EmptyScreen(val message: String) : HomeState
    data class Error(val throwable: Throwable) : HomeState
    data class Success(val saleProducts: List<ProductUI>, val products: List<ProductUI>) : HomeState
    data class User(val userData: UserData) : HomeState
    data class Category(val categoryList: List<String>) : HomeState
    data class ProductsByCategory(val productsByCategory: List<ProductUI>) : HomeState
}