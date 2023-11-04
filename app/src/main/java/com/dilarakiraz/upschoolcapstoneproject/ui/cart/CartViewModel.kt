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

    fun getCartProducts() {
        viewModelScope.launch {
            _cartState.value = CartState.Loading
            when (val result = productRepository.getCartProducts(userRepository.getUserUid())) {
                is Resource.Success -> {
                    val cartData = result.data
                    val totalAmount = cartData.sumOf { it.price }
                    val totalSale = cartData.sumOf { it.salePrice }

                    _cartState.value = CartState.Success(cartData)
                    _cartState.value = CartState.CartData(totalAmount, totalSale)
                }

                is Resource.Error -> CartState.Error(result.throwable)
                is Resource.Fail -> _cartState.value = CartState.Error(Throwable(result.message))
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

    fun changeTotalAmount(priceChange: Double, salePriceChange: Double) {
        if (_cartState.value is CartState.CartData) {
            val currentCartData = (_cartState.value as CartState.CartData)
            val newTotalAmount = currentCartData.totalAmount + priceChange
            val newTotalSale = currentCartData.totalSale + salePriceChange
            _cartState.value = CartState.CartData(newTotalAmount, newTotalSale)
        }
    }
}

sealed interface CartState {
    object Loading : CartState
    data class EmptyScreen(val message: String) : CartState
    data class Success(val products: List<ProductUI>) : CartState
    data class Error(val throwable: Throwable) : CartState
    data class CartData(val totalAmount: Double, val totalSale: Double) : CartState
}