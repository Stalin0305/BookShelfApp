package com.example.bookshelfapp.data.repository.auth

import com.example.bookshelfapp.data.models.AuthState
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun register(userName: String, password: String, email: String, country: String): AuthState
    suspend fun login(email: String, password: String): AuthState
    fun logOut()
    suspend fun getFavourites(uid: String): List<String>

    suspend fun addToFavourites(uid: String, bookUid: String): List<String>
    suspend fun removeFromFavourites(uid: String, bookUid: String): List<String>
}