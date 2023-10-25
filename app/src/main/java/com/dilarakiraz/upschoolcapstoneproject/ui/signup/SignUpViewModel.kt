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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    private val db = Firebase.firestore

    private fun signUp(email: String, password: String, nickname: String, phoneNumber: String) {
        viewModelScope.launch {
            _state.value = SignUpState.Loading
            when (val result = userRepository.signUp(email, password)) {
                is Resource.Success -> {
                    if (result.data) {
                        saveUserDataToFirestore(email, nickname, phoneNumber)
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

    private fun saveUserDataToFirestore(email: String, nickname: String, phoneNumber: String) {
        val user = hashMapOf(
            "nickname" to nickname,
            "phone_number" to phoneNumber
        )
        db.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                _state.value = SignUpState.GoToHome
            }
            .addOnFailureListener { e ->
                _state.value = SignUpState.Error(e)
            }
    }

    fun checkInfo(email: String, password: String, nickname: String, phoneNumber: String) {
        if (email.isEmpty() || password.isEmpty() || nickname.isEmpty() || phoneNumber.isEmpty()) {
            showError(R.string.fill_all_fields)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(R.string.invalid_mail)
        } else if (password.length <= 6) {
            showError(R.string.invalid_password)
        } else {
            signUp(email, password, nickname, phoneNumber)
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