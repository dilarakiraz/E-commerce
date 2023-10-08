package com.dilarakiraz.upschoolcapstoneproject.ui.signup

import android.content.Context
import android.util.Patterns
import android.widget.Toast
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

    private fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _state.value = SignUpState.Loading
            when (val result = userRepository.signUp(email, password)) {
                is Resource.Success -> {
                    _state.value = if (result.data) {
                        SignUpState.GoToHome
                    } else {
                        SignUpState.Error(Throwable(stringRes(R.string.something_went_wrong)))
                    }
                }

                is Resource.Error -> {
                    _state.value = SignUpState.Error(result.throwable)
                }

                is Resource.Fail -> {

                }

                else -> {}
            }
        }
    }

    fun checkInfo(email: String, password: String) {
        when{
            Patterns.EMAIL_ADDRESS.matcher(email).matches().not() -> {
                _state.value = SignUpState.Error(Throwable(stringRes(R.string.invalid_mail)))
            }

            password.isEmpty() || password.length <= 6 ->{
                _state.value = SignUpState.Error(Throwable(stringRes(R.string.invalid_password)))
            }

            else -> signUp(email,password)
        }
    }
}

sealed interface SignUpState {
    object Loading : SignUpState
    object GoToHome : SignUpState
    data class Error(val throwable: Throwable) : SignUpState
}