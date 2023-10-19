package com.dilarakiraz.upschoolcapstoneproject.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * Created on 19.10.2023
 * @author Dilara Kiraz
 */

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _userProfile = MutableLiveData<Resource<DocumentSnapshot>>()
    val userProfile: LiveData<Resource<DocumentSnapshot>> = _userProfile

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

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
}