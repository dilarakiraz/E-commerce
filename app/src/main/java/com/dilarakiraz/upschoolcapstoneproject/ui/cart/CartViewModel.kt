package com.dilarakiraz.upschoolcapstoneproject.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.repository.ProductRepository
import com.dilarakiraz.upschoolcapstoneproject.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CartViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private var _cartState = MutableLiveData<CartState>()
    val cartState: LiveData<CartState>
        get() = _cartState

    private val _totalAmount = MutableLiveData(0.0)
    val totalAmount: LiveData<Double> = _totalAmount

    private val _updatedCart =
        MutableLiveData<List<ProductUI>>()
    val updatedCart: LiveData<List<ProductUI>>
        get() = _updatedCart

    private val _cartItems = MutableLiveData<List<ProductUI>>()
    val cartItems: LiveData<List<ProductUI>> = _cartItems


    fun getCartProducts() {
        viewModelScope.launch {
            _cartState.value = CartState.Loading
            _cartState.value =
                when (val result = productRepository.getCartProducts(userRepository.getUserUid())) {
                    is Resource.Success -> CartState.Success(result.data)
                    is Resource.Error -> CartState.Error(result.throwable)
                    is Resource.Fail -> CartState.EmptyScreen(result.message)
            }
        }
        loadUpdatedCart()
    }

    fun deleteProductFromCart(id: Int) {
        viewModelScope.launch {
            productRepository.deleteFromCart(id)
            getCartProducts()
            loadUpdatedCart()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            productRepository.clearCart(userRepository.getUserUid())
            getCartProducts()
            loadUpdatedCart()
        }
    }

    private fun loadUpdatedCart() {
        viewModelScope.launch {
            when (val result = productRepository.getCartProducts(userRepository.getUserUid())) {
                is Resource.Success -> {
                    _updatedCart.value = result.data
                }
                else -> {
                    _updatedCart.value = emptyList()
                }
            }
        }
    }
    fun increase(price: Double) {
        _totalAmount.value = _totalAmount.value?.plus(price)
    }

    fun decrease(price: Double) {
        _totalAmount.value = _totalAmount.value?.minus(price)
    }
}

sealed interface CartState {
    object Loading : CartState
    data class EmptyScreen(val message: String) : CartState
    data class Success(val products: List<ProductUI>) : CartState
    data class Error(val throwable: Throwable) : CartState
}