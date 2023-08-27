package com.example.bookshelfapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserInfo(
    val name: String,
    val email: String,
    val uid: String,
    val country: String,
    val favouritesList: ArrayList<String> = arrayListOf()
) : Parcelable
