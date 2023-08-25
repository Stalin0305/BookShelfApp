package com.example.bookshelfapp.data.models

data class BookInfo(
    val id : String,
    val image : String,
    val hits : Int,
    val alias : String,
    val title : String,
    val lastChapterDate : Int
)
