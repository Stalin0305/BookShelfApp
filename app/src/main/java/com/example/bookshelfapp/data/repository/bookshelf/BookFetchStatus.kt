package com.example.bookshelfapp.data.repository.bookshelf

import com.example.bookshelfapp.data.models.BookInfo

sealed class BookFetchStatus {
    data class BookFetchSuccess(val bookList: List<BookInfo>): BookFetchStatus()

    data class BookFetchFailure(val errorMessage: String): BookFetchStatus()

}
