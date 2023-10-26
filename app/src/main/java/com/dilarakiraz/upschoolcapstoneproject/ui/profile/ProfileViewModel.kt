package com.dilarakiraz.upschoolcapstoneproject.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * Created on 19.10.2023
 * @author Dilara Kiraz
 */

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _userProfile = MutableLiveData<Resource<DocumentSnapshot>>()
    val userProfile: LiveData<Resource<DocumentSnapshot>> = _userProfile

    fun performProfileAction(imageUri: Uri? = null) {
        val user = firebaseAuth.currentUser

        user?.let { currentUser ->
            val userId = currentUser.uid
            val userDocRef = firestore.collection("users").document(userId)

            if (imageUri != null) {
                userDocRef.update("profileImageUrl", imageUri.toString())
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e ->
                        _userProfile.value = Resource.Error(e)
                    }
            } else {
                userDocRef.get()
                    .addOnSuccessListener { document ->
                        _userProfile.value = Resource.Success(document)
                    }
                    .addOnFailureListener { e ->
                        _userProfile.value = Resource.Error(e)
                    }
            }
        }
    }
}

sealed interface DetailState {
    object Loading : DetailState
    data class Success(val product: ProductUI) : DetailState
    data class Error(val throwable: Throwable) : DetailState
}