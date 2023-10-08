package com.dilarakiraz.upschoolcapstoneproject.data.repository

import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firebaseAuth: FirebaseAuth,
) {
    fun checkUserLogin(): Boolean = firebaseAuth.currentUser != null

    fun getUserUid(): String = firebaseAuth.currentUser?.uid.orEmpty()

    suspend fun signUp(email: String, password: String): Resource<Boolean> {
        return try {
            val signUpTask = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            if (signUpTask.user != null) {
                Resource.Success(true)
            } else {
                Resource.Success(false)
            }
//            Resource.Success(signUpTask.user != null)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    suspend fun signIn(email: String, password: String): Resource<Boolean> {
        return try {
            val signInTask = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (signInTask.user != null) {
                Resource.Success(true)
            } else {
                Resource.Success(false)
            }
            // Resource.Success(signInTask.user != null)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}