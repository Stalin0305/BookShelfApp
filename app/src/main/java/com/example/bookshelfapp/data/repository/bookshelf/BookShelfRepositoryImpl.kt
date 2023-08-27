package com.example.bookshelfapp.data.repository.bookshelf

import com.example.bookshelfapp.data.models.BookInfo
import com.example.bookshelfapp.data.services.BookServiceApi
import retrofit2.Response
import javax.inject.Inject

class BookShelfRepositoryImpl @Inject constructor(
    private val bookServiceApi: BookServiceApi
) : BookShelfRepository {
    override suspend fun getBooksList(): Response<List<BookInfo>> {
        return bookServiceApi.getBooks()
    }
}