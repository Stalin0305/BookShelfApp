package com.example.bookshelfapp.data.services

import com.example.bookshelfapp.data.models.BookInfo
import retrofit2.Response
import retrofit2.http.GET

interface BookServiceApi {

    @GET("/b/ZEDF")
    suspend fun getBooks(): Response<List<BookInfo>>
}