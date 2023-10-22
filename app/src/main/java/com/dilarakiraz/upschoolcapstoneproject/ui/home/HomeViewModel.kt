package com.dilarakiraz.upschoolcapstoneproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.repository.ProductRepository
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
}

sealed interface HomeState {
    object Loading : HomeState
    data class EmptyScreen(val message: String) : HomeState
    data class Error(val throwable: Throwable) : HomeState
    data class Success(val saleProducts: List<ProductUI>, val products: List<ProductUI>) : HomeState
}