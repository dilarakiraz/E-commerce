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

    fun getCartProducts() {
        viewModelScope.launch {
            _cartState.value = CartState.Loading

            when (val result = productRepository.getCartProducts(userRepository.getUserUid())) {

                is Resource.Success -> {
                    _cartState.value = CartState.Success(result.data)
                    _totalAmount.value = result.data.sumOf { it.price }
                }

                is Resource.Error -> CartState.Error(result.throwable)

                is Resource.Fail -> {
                    if (result.message.contains("Cart is empty", true)) {
                        _cartState.value = CartState.EmptyScreen("Sepetiniz şu an boş.")
                    } else {
                        _cartState.value = CartState.Error(Throwable(result.message))
                    }
                }
            }
        }
    }

    fun deleteProductFromCart(id: Int) {
        viewModelScope.launch {
            productRepository.deleteFromCart(id)
            getCartProducts()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            productRepository.clearCart(userRepository.getUserUid())
            getCartProducts()
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