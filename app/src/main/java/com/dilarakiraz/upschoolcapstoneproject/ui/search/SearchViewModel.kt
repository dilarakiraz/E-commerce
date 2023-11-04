package com.dilarakiraz.upschoolcapstoneproject.ui.search

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

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private var _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState>
        get() = _state

    private var query = ""

    fun searchProduct(query: String?) = viewModelScope.launch {
        if (query.isNullOrEmpty().not()) {
            this@SearchViewModel.query = query.orEmpty()

            _state.value = SearchState.Loading

            _state.value = when (val result = productRepository.searchProduct(query.orEmpty())) {
                is Resource.Success -> SearchState.Success(result.data)
                is Resource.Error -> SearchState.Error(result.throwable)
                is Resource.Fail -> SearchState.EmptyScreen(result.message)
            }
        }
    }

    fun setFavoriteState(product: ProductUI) = viewModelScope.launch {
        if (product.isFavorite) productRepository.deleteFromFavorites(product)
        else productRepository.addToFavorites(product)
        searchProduct(query)
    }
}

sealed interface SearchState {
    object Loading : SearchState
    data class EmptyScreen(val message: String) : SearchState
    data class Success(val products: List<ProductUI>) : SearchState
    data class Error(val throwable: Throwable) : SearchState
}