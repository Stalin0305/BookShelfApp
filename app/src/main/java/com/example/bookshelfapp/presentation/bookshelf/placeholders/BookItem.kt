package com.example.bookshelfapp.presentation.bookshelf.placeholders

data class BookItem(
    val id : String,
    val image : String,
    val hits : Int,
    val alias : String,
    val title : String,
    val lastChapterDate : Int,
    var isFavourite: Boolean = false
)
