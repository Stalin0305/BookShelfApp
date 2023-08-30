package com.example.bookshelfapp.data.mappers

import com.example.bookshelfapp.data.models.BookInfo
import com.example.bookshelfapp.presentation.bookshelf.placeholders.BookItem

object BookInfoMapper {

    fun mapBookInfoNetworkResponse(
        bookList: List<BookInfo>,
        favouriteList: ArrayList<String>?
    ): List<BookItem> {
        val bookItemList = mutableListOf<BookItem>()
        bookList.forEach { book ->
            val bookItem = BookItem(
                id = book.id,
                image = book.image,
                hits = book.hits,
                alias = book.alias,
                title = book.title,
                lastChapterDate = book.lastChapterDate,
            )
            if (favouriteList?.contains(book.id) == true) {
                bookItem.isFavourite = true
            }
            bookItemList.add(bookItem)
        }
        return bookItemList
    }
}