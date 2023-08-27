package com.example.bookshelfapp.data.usecase

import com.example.bookshelfapp.data.repository.bookshelf.BookFetchStatus
import com.example.bookshelfapp.data.repository.bookshelf.BookShelfRepository
import com.example.bookshelfapp.data.utils.BooksOrder
import com.example.bookshelfapp.data.utils.OrderType
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val bookShelfRepository: BookShelfRepository
) {
    suspend operator fun invoke(
        booksOrder: BooksOrder = BooksOrder.Title(OrderType.Descending)
    ): BookFetchStatus {
        val response = bookShelfRepository.getBooksList()
        return if (response.isSuccessful) {
            val notesList = response.body()
            if (notesList.isNullOrEmpty()) {
                BookFetchStatus.BookFetchFailure("Null or empty value received")
            } else {
                BookFetchStatus.BookFetchSuccess(notesList)
            }
        } else {
            BookFetchStatus.BookFetchFailure(response.errorBody().toString())
        }
    }
}