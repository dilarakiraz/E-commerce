package com.dilarakiraz.upschoolcapstoneproject.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created on 8.10.2023
 * @author Dilara Kiraz
 */

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel(){

    private var _detailState = MutableLiveData<DetailState>(DetailState.Loading)
    val detailState: LiveData<DetailState>
        get() = _detailState

    private var _selectedProduct = MutableLiveData<ProductUI>()
    val selectedProduct: LiveData<ProductUI>
        get() = _selectedProduct


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
}

sealed interface DetailState{
    object Loading : DetailState
    data class EmptyScreen(val message: String): DetailState
    data class Success(val product: ProductUI): DetailState
    data class Error(val throwable: Throwable): DetailState
}