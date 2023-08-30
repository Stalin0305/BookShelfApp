package com.example.bookshelfapp.data.repository.auth

import android.util.Log
import com.example.bookshelfapp.data.models.AuthState
import com.example.bookshelfapp.data.models.UserInfo
import com.example.bookshelfapp.data.utils.Constants.COUNTRY
import com.example.bookshelfapp.data.utils.Constants.EMAIL
import com.example.bookshelfapp.data.utils.Constants.FAVOURITE_LIST
import com.example.bookshelfapp.data.utils.Constants.NAME
import com.example.bookshelfapp.data.utils.Constants.UID
import com.example.bookshelfapp.data.utils.Constants.USER_COLLECTION
import com.example.bookshelfapp.data.utils.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val TAG = "AuthRepositoryImpl"

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: FirebaseUser? = firebaseAuth.currentUser

    override suspend fun register(
        userName: String,
        password: String,
        email: String,
        country: String
    ): AuthState {
        return try {
            var state: AuthState = AuthState.Failure("Something went wrong")
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid?.let { uid ->
                val user = UserInfo(
                    name = userName,
                    email = email,
                    uid = uid,
                    country = country
                )

                state = suspendCoroutine<AuthState> { continuation ->
                    fireStore.collection(USER_COLLECTION).document(uid).set(user)
                        .addOnSuccessListener {
                            continuation.resume(AuthState.Success(user))
                        }
                        .addOnFailureListener {
                            continuation.resume(AuthState.Failure(it.message ?: "Unknown error"))
                        }
                }
            }
            return state
        } catch (e: Exception) {
            e.printStackTrace()
            AuthState.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun login(email: String, password: String): AuthState {
        return try {
            var state: AuthState = AuthState.Failure("Something went wrong")
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.uid?.let { uid ->
                state = suspendCoroutine { continuation ->
                    fireStore.collection(USER_COLLECTION).document(uid).get()
                        .addOnSuccessListener { document ->
                            document?.data?.let { data ->
                                val userInfo = UserInfo(
                                    name = data[NAME].toString(),
                                    email = data[EMAIL].toString(),
                                    uid = data[UID].toString(),
                                    country = data[COUNTRY].toString(),
                                    favouritesList = data[FAVOURITE_LIST] as? ArrayList<String>
                                        ?: arrayListOf()
                                )
                                continuation.resume(AuthState.Success(userInfo))
                            }
                        }
                        .addOnFailureListener {
                            continuation.resume(AuthState.Failure(it.message ?: "Unknown error"))
                        }
                }

            }
            return state
        } catch (e: Exception) {
            e.printStackTrace()
            AuthState.Failure(e.message ?: "Unknown error")
        }
    }

    override fun logOut() {
        firebaseAuth.signOut()
    }

    override suspend fun getFavourites(uid: String): ArrayList<String> {
        return try {
            withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    fireStore.collection(USER_COLLECTION).document(uid).get()
                        .addOnSuccessListener { document ->
                            document?.data?.let { data ->
                                continuation.resume(
                                    data[FAVOURITE_LIST] as? ArrayList<String> ?: arrayListOf()
                                )

                            }
                        }
                        .addOnFailureListener {
                            continuation.resume(arrayListOf())
                        }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Exception: ${e.message}")
            arrayListOf<String>()
        }
    }

    override suspend fun addToFavourites(uid: String, bookUid: String): ArrayList<String> {
        return try {
            withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    val docRef = fireStore.collection(USER_COLLECTION).document(uid)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val data = document.data
                                val favouriteList =
                                    data?.get(FAVOURITE_LIST) as? ArrayList<String> ?: arrayListOf()
                                favouriteList.add(bookUid)
                                docRef.update(FAVOURITE_LIST, favouriteList)
                                    .addOnSuccessListener {
                                        continuation.resume(favouriteList)
                                    }
                                    .addOnFailureListener {
                                        Log.d(TAG, "Unable to add to favourites")
                                        continuation.resume(arrayListOf())
                                    }
                            }
                        }
                        .addOnFailureListener {
                            continuation.resume(arrayListOf())
                        }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Exception: ${e.message}")
            arrayListOf<String>()
        }
    }

    override suspend fun removeFromFavourites(uid: String, bookUid: String): ArrayList<String> {
        return try {
            withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    val docRef = fireStore.collection(USER_COLLECTION).document(uid)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val data = document.data
                                val favouriteList =
                                    data?.get(FAVOURITE_LIST) as? ArrayList<String> ?: arrayListOf()
                                if (favouriteList.contains(bookUid)) {
                                    favouriteList.remove(bookUid)
                                    docRef.update(FAVOURITE_LIST, favouriteList)
                                        .addOnSuccessListener {
                                            continuation.resume(favouriteList)
                                        }
                                        .addOnFailureListener {
                                            Log.d(TAG, "Unable to remove from favourites")
                                            continuation.resume(arrayListOf())
                                        }
                                }

                            }
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "Unable to fetch document")
                            continuation.resume(arrayListOf())
                        }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Exception: ${e.message}")
            arrayListOf<String>()
        }
    }
}