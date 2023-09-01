package com.example.bookshelfapp.presentation.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelfapp.BookShelfApp
import com.example.bookshelfapp.data.models.AuthState
import com.example.bookshelfapp.data.models.CountryData
import com.example.bookshelfapp.data.models.UserInfo
import com.example.bookshelfapp.data.repository.auth.AuthRepositoryImpl
import com.example.bookshelfapp.utils.EMPTY_STRING
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repositoryImpl: AuthRepositoryImpl,
    private val application: BookShelfApp
): ViewModel() {

    private val _registrationFlow = MutableStateFlow<AuthState?>(null)
    val registrationFlow: StateFlow<AuthState?> = _registrationFlow

    private val _loginFlow = MutableStateFlow<AuthState?>(null)
    val loginFlow: StateFlow<AuthState?> = _loginFlow
    var currentUser: UserInfo? = null
    var countryList = listOf<String>()

    var userName: String = EMPTY_STRING
    var email: String = EMPTY_STRING
    var password: String = EMPTY_STRING
    var selectedCountry: String = EMPTY_STRING

    init {
        if (repositoryImpl.currentUser != null && currentUser != null) {
            _loginFlow.value = AuthState.Success(currentUser!!)
        }
        getCountryDetails(application.applicationContext)
    }

    fun getCountryDetails(context: Context) {
        val assetManager = context.assets
        val inputStream = assetManager.open("country.json")
        val jsonText = inputStream.bufferedReader().use { it.readText() }

        val gson = Gson()
        val jsonObject = gson.fromJson(jsonText, JsonObject::class.java)
        val countryDataObject = jsonObject.getAsJsonObject("data")
        val countryMap = mutableMapOf<String, CountryData>()
        for ((countryCode, countryJson) in countryDataObject.entrySet()) {
            val countryData = gson.fromJson(countryJson, CountryData::class.java)
            countryMap[countryCode] = countryData
        }
        countryList = countryMap.values.map { it.country }.sorted()
    }

    fun register(email: String, password: String, userName: String, country: String) = viewModelScope.launch {
        _registrationFlow.value = AuthState.Loading
        val result = repositoryImpl.register(userName, password, email, country)
        _registrationFlow.value = result
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginFlow.value = AuthState.Loading
        val result = repositoryImpl.login(email, password)
        if (result is AuthState.Success) {
            currentUser = result.userInfo
        }
        _loginFlow.value = result
    }

    fun logOut() {
        repositoryImpl.logOut()
    }
}