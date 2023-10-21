package com.dilarakiraz.upschoolcapstoneproject.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject


/**
 * Created on 19.10.2023
 * @author Dilara Kiraz
 */

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<Resource<DocumentSnapshot>>()
    val userProfile: LiveData<Resource<DocumentSnapshot>> = _userProfile

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    val currentUser = firebaseAuth.currentUser
    val userUID = currentUser?.uid

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user = firebaseAuth.currentUser

        if (user != null) {
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    _userProfile.value = Resource.Success(document)
                }
                .addOnFailureListener { e ->
                    _userProfile.value = Resource.Error(e)
                }
        } else {
            _userProfile.value = Resource.Error(Exception("Kullanıcı oturumu açık değil."))
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val userUID = currentUser?.uid

        if (userUID != null) {
            val userDocRef = db.collection("users").document(userUID)
            val updateData = hashMapOf<String, Any?>(
                "profileImage" to imageUri.toString()
            )

            userDocRef.update(updateData)
                .addOnSuccessListener {
                }
                .addOnFailureListener { e -> }
        }
    }
}

sealed interface DetailState {
    object Loading : DetailState
    data class EmptyScreen(val message: String) : DetailState
    data class Success(val product: ProductUI) : DetailState
    data class Error(val throwable: Throwable) : DetailState
}