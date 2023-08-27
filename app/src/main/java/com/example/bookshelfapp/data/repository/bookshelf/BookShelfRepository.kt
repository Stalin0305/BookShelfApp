package com.example.bookshelfapp.data.repository.bookshelf

import com.example.bookshelfapp.data.models.BookInfo
import retrofit2.Response

interface BookShelfRepository {

    suspend fun getBooksList(): Response<List<BookInfo>>
}