package com.dilarakiraz.upschoolcapstoneproject.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.repository.ProductRepository
import com.dilarakiraz.upschoolcapstoneproject.ui.cart.CartState
import com.dilarakiraz.upschoolcapstoneproject.utilities.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val stringRes: ResourceProvider
) : ViewModel() {

    private var _state = MutableLiveData<FavoritesState>(FavoritesState.Loading)
    val state: LiveData<FavoritesState>
        get() = _state

    fun getFavorites() = viewModelScope.launch {
        _state.value = when (val result = productRepository.getFavorites()) {
            is Resource.Success -> FavoritesState.Success(result.data)
            is Resource.Error -> FavoritesState.Error(result.throwable)
            is Resource.Fail -> FavoritesState.EmptyData(stringRes(R.string.something_went_wrong))
        }
    }

    fun deleteFromFavorites(product: ProductUI) = viewModelScope.launch {
        productRepository.deleteFromFavorites(product)
        getFavorites()
    }

    fun clearAllFavorites() = viewModelScope.launch {
        when (val result = productRepository.clearFavorites()) {
            is Resource.Success -> {
                _state.value = FavoritesState.Success(emptyList())
            }

            is Resource.Error -> {
                _state.value = FavoritesState.Error(result.throwable)
            }

            is Resource.Fail -> {
                _state.value = FavoritesState.Error(Throwable(result.message))
            }
        }
    }
}

sealed interface FavoritesState {
    object Loading : FavoritesState
    data class Success(val favoriteProducts: List<ProductUI>) : FavoritesState
    data class Error(val throwable: Throwable) : FavoritesState
    data class EmptyData(val message: String) : FavoritesState
    data class EmptyScreen(val message: String) : FavoritesState
}