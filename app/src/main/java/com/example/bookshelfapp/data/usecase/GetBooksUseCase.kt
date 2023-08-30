package com.example.bookshelfapp.data.usecase

import android.util.Log
import com.example.bookshelfapp.data.repository.bookshelf.BookFetchStatus
import com.example.bookshelfapp.data.repository.bookshelf.BookShelfRepository
import com.example.bookshelfapp.data.utils.BooksOrder
import com.example.bookshelfapp.data.utils.OrderType
import javax.inject.Inject

class GetBooksUseCase(
    private val bookShelfRepository: BookShelfRepository
) {
    suspend operator fun invoke(
        booksOrder: BooksOrder = BooksOrder.Title(OrderType.Descending)
    ): BookFetchStatus {
        return try {
            val response = bookShelfRepository.getBooksList()
            if (response.isSuccessful) {
                val notesList = response.body()
                if (notesList.isNullOrEmpty()) {
                    BookFetchStatus.BookFetchFailure("Null or empty value received")
                } else {
                    BookFetchStatus.BookFetchSuccess(notesList)
                }
            } else {
                BookFetchStatus.BookFetchFailure(response.errorBody().toString())
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("GetBooksUseCase", "Exception: ${e.message}")
            BookFetchStatus.BookFetchFailure(e.message.toString())
        }
    }
}