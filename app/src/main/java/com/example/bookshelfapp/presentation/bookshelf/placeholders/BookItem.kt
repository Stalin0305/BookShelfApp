package com.example.bookshelfapp.presentation.bookshelf.placeholders

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookItem(
    val id : String,
    val image : String,
    val hits : Int,
    val alias : String,
    val title : String,
    val lastChapterDate : Int,
    var isFavourite: Boolean = false
): Parcelable