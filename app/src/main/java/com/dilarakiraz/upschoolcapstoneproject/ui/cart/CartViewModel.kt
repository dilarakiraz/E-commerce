package com.dilarakiraz.upschoolcapstoneproject.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI


class CartViewModel (): ViewModel(){

    private var _cartState = MutableLiveData<CartState>()
    val cartState: LiveData<CartState>
        get() = _cartState
}

sealed interface CartState {
    object Loading : CartState
    data class EmptyScreen(val message: String) : CartState
    data class Success(val products: List<ProductUI>) : CartState
    data class Error(val throwable: Throwable) : CartState
}