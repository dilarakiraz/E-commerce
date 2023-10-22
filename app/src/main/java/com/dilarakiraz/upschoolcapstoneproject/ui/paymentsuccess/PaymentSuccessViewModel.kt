package com.dilarakiraz.upschoolcapstoneproject.ui.paymentsuccess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.BaseResponse
import com.dilarakiraz.upschoolcapstoneproject.data.repository.ProductRepository
import com.dilarakiraz.upschoolcapstoneproject.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created on 17.10.2023
 * @author Dilara Kiraz
 */

@HiltViewModel
class PaymentSuccessViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _result = MutableLiveData<Resource<BaseResponse>>()
    val result: LiveData<Resource<BaseResponse>> = _result

    fun clearCart() {
        viewModelScope.launch {
            val result = productRepository.clearCart(userRepository.getUserUid())

            _result.value = when (result) {
                is Resource.Success -> Resource.Success(
                    BaseResponse(
                        status = 200,
                        message = "Cart cleared successfully"
                    )
                )

                is Resource.Error -> Resource.Error(result.throwable)
                else -> Resource.Error(Throwable("Clearing cart failed"))
            }
        }
    }
}