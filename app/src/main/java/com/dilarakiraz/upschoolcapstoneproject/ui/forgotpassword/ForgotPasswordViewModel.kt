package com.dilarakiraz.upschoolcapstoneproject.ui.forgotpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created on 27.10.2023
 * @author Dilara Kiraz
 */

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _result = MutableLiveData<ForgotState>()
    val result: LiveData<ForgotState> = _result

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _result.value = ForgotState.Loading
            val result = userRepository.sendPasswordResetEmail(email)
            when (result) {
                is Resource.Success -> {
                    _result.value = ForgotState.Success("Password reset email sent.")
                }
                is Resource.Fail -> {
                    _result.value = ForgotState.ShowPopUp(result.message)
                }
                is Resource.Error -> {
                    _result.value = ForgotState.ShowPopUp(result.throwable.message ?: "An unexpected error occurred.")
                }
            }
        }
    }
}

sealed interface ForgotState {
    object Loading : ForgotState
    data class Success(val toastMessage: String) : ForgotState
    data class ShowPopUp(val errorMessage: String) : ForgotState
}