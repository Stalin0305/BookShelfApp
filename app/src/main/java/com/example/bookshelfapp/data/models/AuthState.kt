package com.example.bookshelfapp.data.models

import java.lang.Exception

sealed class AuthState() {
   data class Success(val userInfo: UserInfo): AuthState()
    data class Failure(val errorMessage: String): AuthState()
    object Loading: AuthState()
}
