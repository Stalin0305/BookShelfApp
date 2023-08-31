package com.example.bookshelfapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.bookshelfapp.BookShelfApp
import com.example.bookshelfapp.data.utils.Constants

class SharedPreferenceUtil(context: Context) {

    private val sharedPreference =
        context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)


    fun saveString(key: String, value: String) {
        sharedPreference.edit().putString(key, value).apply()
    }

    fun getString(key: String): String {
        return sharedPreference.getString(key, EMPTY_STRING) ?: EMPTY_STRING
    }
}