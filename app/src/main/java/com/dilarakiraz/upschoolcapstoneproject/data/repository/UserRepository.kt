package com.dilarakiraz.upschoolcapstoneproject.data.repository

import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firebaseAuth: FirebaseAuth,
) {

    suspend fun signUp(email: String, password: String): Resource<Boolean> {
        return try {
            val signUpTask = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            if (signUpTask.user != null) {
                Resource.Success(true)
            } else {
                Resource.Fail("User is null")
            }
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
                Resource.Fail("User is null")
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    fun getUserUid(): String = firebaseAuth.currentUser?.uid.orEmpty()
}