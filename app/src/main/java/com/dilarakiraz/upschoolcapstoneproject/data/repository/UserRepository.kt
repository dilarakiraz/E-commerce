package com.dilarakiraz.upschoolcapstoneproject.data.repository

import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firebaseAuth: FirebaseAuth,
    private val productRepository: ProductRepository
) {
    private val db = Firebase.firestore

//    suspend fun signIn(email: String, password: String): Resource<Boolean> {
//        return try {
//            val signInTask = firebaseAuth.signInWithEmailAndPassword(email, password).await()
//
//            if (signInTask.user != null) {
//                Resource.Success(true)
//            } else {
//                Resource.Fail("User is null")
//            }
//        } catch (e: Exception) {
//            Resource.Error(e)
//        }
//    }

    suspend fun signIn(email: String, password: String): Resource<Boolean> =
        firebaseAuthCall { signInWithEmailAndPassword(email, password).await().user != null }


    fun getUserUid(): String = firebaseAuth.currentUser?.uid.orEmpty()

//    suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
//        return try {
//            firebaseAuth.sendPasswordResetEmail(email).await()
//            Resource.Success(Unit)
//        } catch (e: Exception) {
//            Resource.Error(e)
//        }
//    }

    suspend fun sendPasswordResetEmail(email: String): Resource<Unit> =
        firebaseAuthCall { sendPasswordResetEmail(email).await() }

    suspend fun getUserData(userId: String): Resource<UserData> =
        try {
            val userDocument = db.collection("users").document(userId).get().await()
            val nickname = userDocument?.getString("nickname")
            val profileImageUrl = userDocument?.getString("profileImageUrl")

            val cartProductsCount =
                (productRepository.getCartProducts(userId) as? Resource.Success)?.data?.size ?: 0

            Resource.Success(UserData(nickname, profileImageUrl, cartProductsCount))
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun signUpAndSaveUserData(
        email: String,
        password: String,
        nickname: String,
        phoneNumber: String
    ): Resource<Boolean> =
        firebaseAuthCall {
            val signUpTask = createUserWithEmailAndPassword(email, password).await()

            if (signUpTask.user != null) {
                val user = mapOf("nickname" to nickname, "phone_number" to phoneNumber)
                db.collection("users").document(signUpTask.user!!.uid).set(user).await()
                true
            } else {
                false
            }
        }

//    suspend fun getUserData(userId: String): Resource<UserData> {
//        return try {
//            val userDocument = db.collection("users").document(userId).get().await()
//            val nickname = userDocument?.getString("nickname")
//            val profileImageUrl = userDocument?.getString("profileImageUrl")
//
//            val cartProductsResource = productRepository.getCartProducts(userId)
//            val cartProductsCount =
//                if (cartProductsResource is Resource.Success) {
//                    cartProductsResource.data.size
//                } else {
//                    0
//                }
//
//            Resource.Success(UserData(nickname, profileImageUrl, cartProductsCount))
//        } catch (e: Exception) {
//            Resource.Error(e)
//        }
//    }

//    suspend fun signUpAndSaveUserData(
//        email: String,
//        password: String,
//        nickname: String,
//        phoneNumber: String
//    ): Resource<Boolean> {
//        return try {
//            val signUpTask = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
//
//            if (signUpTask.user != null) {
//                val user = hashMapOf(
//                    "nickname" to nickname,
//                    "phone_number" to phoneNumber
//                )
//                db.collection("users")
//                    .document(signUpTask.user!!.uid)
//                    .set(user)
//                    .await()
//                Resource.Success(true)
//            } else {
//                Resource.Fail("User is null")
//            }
//        } catch (e: Exception) {
//            Resource.Error(e)
//        }
//    }

    private suspend inline fun <T : Any> firebaseAuthCall(crossinline authCall: suspend FirebaseAuth.() -> T): Resource<T> =
        try {
            Resource.Success(firebaseAuth.authCall())
        } catch (e: Exception) {
            Resource.Error(e)
        }
}