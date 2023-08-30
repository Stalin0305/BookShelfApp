package com.example.bookshelfapp.presentation.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelfapp.data.mappers.BookInfoMapper
import com.example.bookshelfapp.data.models.BookInfo
import com.example.bookshelfapp.data.models.UserInfo
import com.example.bookshelfapp.data.repository.auth.AuthRepositoryImpl
import com.example.bookshelfapp.data.repository.bookshelf.BookFetchStatus
import com.example.bookshelfapp.data.usecase.GetBooksUseCase
import com.example.bookshelfapp.data.utils.BooksOrder
import com.example.bookshelfapp.data.utils.OrderType
import com.example.bookshelfapp.presentation.bookshelf.placeholders.BookItem
import com.example.bookshelfapp.presentation.bookshelf.placeholders.BookListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookShelfViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val authRepositoryImpl: AuthRepositoryImpl
) : ViewModel() {

    var currentUserInfo: UserInfo? = null

    private val _bookListFlow = MutableStateFlow<BookListUiState?>(null)
    val bookListFlow: StateFlow<BookListUiState?> = _bookListFlow

    fun fetchBookListAndFavourites(order: BooksOrder) {
        _bookListFlow.value = BookListUiState.BookListUILoadingState
        viewModelScope.launch {
            val favouritesFetchJob = async {
                currentUserInfo?.uid?.let { authRepositoryImpl.getFavourites(it) }
            }

            val bookListJob = async {
                getBooksList(order)
            }

            val favouritesList = favouritesFetchJob.await()
            val bookList = bookListJob.await()

            val finalList = mapBookInfoAndFavourites(favouritesList, bookList, order) as MutableList<BookItem>
            if (finalList.isEmpty()) {
                _bookListFlow.value = BookListUiState.BookListUIErrorState
            } else {
                _bookListFlow.value = BookListUiState.BookListUISuccessState(finalList)
            }
        }
    }

    private suspend fun getBooksList(order: BooksOrder): List<BookInfo> {
        var bookInfoList = mutableListOf<BookInfo>()
        when (val result = getBooksUseCase.invoke(order)) {
            is BookFetchStatus.BookFetchSuccess -> {
                bookInfoList = result.bookList as MutableList<BookInfo>
            }

            is BookFetchStatus.BookFetchFailure -> {
                Log.d("BookShelfViewModel", "Api failure")
            }
        }
        return bookInfoList
    }

    private fun mapBookInfoAndFavourites(
        favouriteList: ArrayList<String>?,
        bookList: List<BookInfo>,
        order: BooksOrder
    ): List<BookItem> {
        when (order.orderType) {
            is OrderType.Ascending -> {
                when (order) {
                    is BooksOrder.Title -> {
                        val bookItemList = BookInfoMapper.mapBookInfoNetworkResponse(
                            bookList, favouriteList
                        )
                        bookItemList.sortedBy {
                            it.title
                        }
                        return bookItemList
                    }

                    is BooksOrder.Hits -> {

                        val bookItemList = BookInfoMapper.mapBookInfoNetworkResponse(
                            bookList, favouriteList
                        )
                        bookItemList.sortedBy {
                            it.hits
                        }

                        return bookItemList
                    }

                    is BooksOrder.Favs -> {

                        val bookItemList = BookInfoMapper.mapBookInfoNetworkResponse(
                            bookList, favouriteList
                        )

                        bookItemList.sortedBy {
                            it.title
                        }

                        val modifiedBookList = bookItemList as MutableList<BookItem>

                        bookItemList.forEachIndexed { index, bookItem ->
                            if (bookItem.isFavourite) {
                                modifiedBookList.removeAt(index)
                                modifiedBookList.add(0, bookItem)
                            }
                        }

                        return modifiedBookList
                    }
                }
            }

            is OrderType.Descending -> {
                when (order) {
                    is BooksOrder.Title -> {
                        val bookItemList = BookInfoMapper.mapBookInfoNetworkResponse(
                            bookList, favouriteList
                        )
                        bookItemList.sortedByDescending {
                            it.title
                        }

                        return bookItemList
                    }

                    is BooksOrder.Hits -> {

                        val bookItemList = BookInfoMapper.mapBookInfoNetworkResponse(
                            bookList, favouriteList
                        )
                        bookItemList.sortedByDescending {
                            it.hits
                        }

                        return bookItemList
                    }

                    is BooksOrder.Favs -> {

                        val bookItemList = BookInfoMapper.mapBookInfoNetworkResponse(
                            bookList, favouriteList
                        )

                        bookItemList.sortedBy {
                            it.title
                        }

                        val modifiedBookList = bookItemList as MutableList<BookItem>

                        bookItemList.forEachIndexed { index, bookItem ->
                            if (bookItem.isFavourite) {
                                modifiedBookList.removeAt(index)
                                modifiedBookList.add(bookItem)
                            }
                        }

                        return modifiedBookList
                    }
                }
            }
        }
    }
}