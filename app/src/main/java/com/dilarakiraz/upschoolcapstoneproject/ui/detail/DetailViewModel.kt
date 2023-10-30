package com.dilarakiraz.upschoolcapstoneproject.ui.detail


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

    private var _detailState = MutableLiveData<DetailState>(DetailState.EmptyScreen(""))
    val detailState: LiveData<DetailState>
        get() = _detailState

    private var _product = MutableLiveData<ProductUI>()
    val product: LiveData<ProductUI>
        get() = _product

    fun getProductDetail(id: Int) {
        viewModelScope.launch {
            _detailState.value = DetailState.Loading
            when (val result = productRepository.getProductDetail(id)) {
                is Resource.Success -> {
                    _detailState.value = DetailState.Success(result.data)
                    _product.value = result.data
                }

                is Resource.Error -> DetailState.ShowPopUp(
                    result.throwable.message ?: "Beklenmeyen bir hata oluştu."
                )

                is Resource.Fail -> DetailState.EmptyScreen(result.message)
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            product.value?.let {
                if (it.isFavorite) {
                    productRepository.deleteFromFavorites(it)
                } else {
                    productRepository.addToFavorites(it)
                }
                getProductDetail(it.id)
            }
        }
    }

    fun addToCart(productId: Int) {
        viewModelScope.launch {
            val result = productRepository.addToCart(getUserUid(), productId)

            if (result is Resource.Success) {
                val productValue = product.value
                if (productValue != null) {
                    _detailState.value = DetailState.Success(productValue, "Ürün sepete eklendi.")
                }
            }
        }
    }

    fun getUserUid(): String {
        return userRepository.getUserUid()
    }
}

sealed interface DetailState {
    object Loading : DetailState
    data class EmptyScreen(val message: String) : DetailState
    data class Success(val product: ProductUI, val toastMessage: String? = null) : DetailState
    //data class Error(val throwable: Throwable) : DetailState
    data class ShowPopUp(val errorMessage: String) : DetailState
}