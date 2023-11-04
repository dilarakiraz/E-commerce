package com.dilarakiraz.upschoolcapstoneproject.ui.signup


import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.repository.UserRepository
import com.dilarakiraz.upschoolcapstoneproject.utilities.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val stringRes: ResourceProvider
) : ViewModel() {

    private var _state = MutableLiveData<SignUpState>()
    val state: LiveData<SignUpState>
        get() = _state

    suspend fun signUpAndSaveUserData(
        email: String,
        password: String,
        nickname: String,
        phoneNumber: String
    ) {
        _state.value = SignUpState.Loading
        when (val result =
            userRepository.signUpAndSaveUserData(email, password, nickname, phoneNumber)) {
            is Resource.Success -> {
                if (result.data) {
                    _state.value = SignUpState.GoToHome
                } else {
                    showError(R.string.something_went_wrong)
                }
            }

            is Resource.Error -> {
                _state.value = SignUpState.Error(result.throwable)
            }

            is Resource.Fail -> {
                _state.value = SignUpState.Error(Throwable(stringRes(R.string.network_error)))
            }
        }
    }

    private fun showError(@StringRes errorMessageResId: Int) {
        val errorMessage = stringRes(errorMessageResId)
        _state.value = SignUpState.Error(Throwable(errorMessage))
    }
}

sealed interface SignUpState {
    object Loading : SignUpState
    object GoToHome : SignUpState
    data class Error(val throwable: Throwable) : SignUpState
}