package com.dilarakiraz.upschoolcapstoneproject.ui.detail


import android.content.Context
import android.widget.Toast
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


/**
 * Created on 8.10.2023
 * @author Dilara Kiraz
 */

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var _detailState = MutableLiveData<DetailState>(DetailState.Loading)
    val detailState: LiveData<DetailState>
        get() = _detailState

    private var _selectedProduct = MutableLiveData<ProductUI>()
    val selectedProduct: LiveData<ProductUI>
        get() = _selectedProduct

    private var isFavoriteUpdating = false

    fun getProductDetail(id: Int) {
        viewModelScope.launch {
            _detailState.value = DetailState.Loading
            when (val result = productRepository.getProductDetail(id)) {
                is Resource.Success -> {
                    _detailState.value = DetailState.Success(result.data)
                    _selectedProduct.value = result.data // Seçilen ürünü güncelle
                }

                is Resource.Error -> _detailState.value = DetailState.Error(result.throwable)
                is Resource.Fail -> _detailState.value = DetailState.EmptyScreen(result.message)
            }
        }
    }

    fun toggleFavorite(product: ProductUI) {
        isFavoriteUpdating = true
        viewModelScope.launch {
            if (product.isFavorite) {
                productRepository.deleteFromFavorites(product)
                _selectedProduct.value = product.copy(isFavorite = false)
            } else {
                productRepository.addToFavorites(product)
                _selectedProduct.value = product.copy(isFavorite = true)
            }
            isFavoriteUpdating = false
        }
    }

    fun isFavoriteUpdating(): Boolean {
        return isFavoriteUpdating
    }

    fun addToCart(userId: String, productId: Int, context: Context) {
        viewModelScope.launch {
            val result = productRepository.addToCart(userId, productId)
            if (result is Resource.Success && result.data) {
                Toast.makeText(context, "Ürün başarıyla sepete eklendi.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Ürün sepete eklenemedi.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun isUserAuthenticated(): Boolean {
        return userRepository.isUserAuthenticated()
    }

    fun getUserUid(): String {
        return userRepository.getUserUid()
    }
}


sealed interface DetailState {
    object Loading : DetailState
    data class EmptyScreen(val message: String) : DetailState
    data class Success(val product: ProductUI) : DetailState
    data class Error(val throwable: Throwable) : DetailState
}