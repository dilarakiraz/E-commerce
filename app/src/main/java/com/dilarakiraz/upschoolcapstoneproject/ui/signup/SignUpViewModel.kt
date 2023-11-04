package com.dilarakiraz.upschoolcapstoneproject.ui.signup


import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.repository.UserRepository
import com.dilarakiraz.upschoolcapstoneproject.utilities.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val stringRes: ResourceProvider
) : ViewModel() {

    private var _state = MutableLiveData<SignUpState>()
    val state: LiveData<SignUpState>
        get() = _state

    private fun signUp(email: String, password: String, nickname: String, phoneNumber: String) {
        viewModelScope.launch {
            _state.value = SignUpState.Loading
            when (val result = userRepository.signUp(email, password)) {
                is Resource.Success -> {
                    if (result.data) {
                        val saveResult =
                            userRepository.saveUserDataToFirestore(email, nickname, phoneNumber)
                        if (saveResult is Resource.Success) {
                            _state.value = SignUpState.GoToHome
                        } else {
                            _state.value =
                                SignUpState.Error(Throwable(stringRes(R.string.something_went_wrong)))
                        }
                    } else {
                        _state.value =
                            SignUpState.Error(Throwable(stringRes(R.string.something_went_wrong)))
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
    }

    fun checkInfo(email: String, password: String, nickname: String, phoneNumber: String) {
        when {
            email.isEmpty() || password.isEmpty() || nickname.isEmpty() || phoneNumber.isEmpty() -> showError(
                R.string.fill_all_fields
            )

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> showError(R.string.invalid_mail)
            password.length <= 6 -> showError(R.string.invalid_password)
            else -> signUp(email, password, nickname, phoneNumber)
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