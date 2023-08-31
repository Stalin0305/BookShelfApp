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
import com.example.bookshelfapp.presentation.bookshelf.placeholders.AddFavouriteUIState
import com.example.bookshelfapp.presentation.bookshelf.placeholders.BookItem
import com.example.bookshelfapp.presentation.bookshelf.placeholders.BookListUiState
import com.example.bookshelfapp.presentation.bookshelf.placeholders.RemoveFavouriteUIState
import com.example.bookshelfapp.utils.EMPTY_STRING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BookShelfViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val authRepositoryImpl: AuthRepositoryImpl
) : ViewModel() {

    var userUid: String? = null

    private val _bookListFlow = MutableStateFlow<BookListUiState?>(null)
    val bookListFlow: StateFlow<BookListUiState?> = _bookListFlow

    private val _addFavouriteFlow = MutableStateFlow<AddFavouriteUIState?>(null)
    val addFavouriteFlow: StateFlow<AddFavouriteUIState?> = _addFavouriteFlow

    private val _removeFavouriteFlow = MutableStateFlow<RemoveFavouriteUIState?>(null)
    val removeFavouriteFlow: StateFlow<RemoveFavouriteUIState?> = _removeFavouriteFlow

    var finalBookItemList: MutableList<BookItem> = mutableListOf()
    private var bookList: List<BookInfo> = mutableListOf()
    private var favouritesList: ArrayList<String>? = arrayListOf()

    var bookDetailItem: BookItem? = null

    var currentSortOrderType: OrderType = OrderType.Ascending
    var currentSortType: BooksOrder = BooksOrder.Title(currentSortOrderType)

    fun fetchBookListAndFavourites(order: BooksOrder) {
        _bookListFlow.value = BookListUiState.BookListUILoadingState
        viewModelScope.launch(Dispatchers.IO) {
            val favouritesFetchJob = async {
                userUid?.let { authRepositoryImpl.getFavourites(it) }
            }

            val bookListJob = async {
                getBooksList(order)
            }

            favouritesList = favouritesFetchJob.await()
            bookList = bookListJob.await()

            finalBookItemList = mapBookInfoAndFavourites(favouritesList, bookList, order) as MutableList<BookItem>
            if (finalBookItemList.isEmpty()) {
                _bookListFlow.value = BookListUiState.BookListUIErrorState
            } else {
                _bookListFlow.value = BookListUiState.BookListUISuccessState(finalBookItemList)
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
                        return bookItemList.sortedBy {
                            it.title.lowercase()
                        }
                    }

                    is BooksOrder.Hits -> {

                        val bookItemList = BookInfoMapper.mapBookInfoNetworkResponse(
                            bookList, favouriteList
                        )
                        return bookItemList.sortedBy {
                            it.hits
                        }
                    }

                    is BooksOrder.Favs -> {

                        val bookItemList = BookInfoMapper.mapBookInfoNetworkResponse(
                            bookList, favouriteList
                        )

                        val sortedList = bookItemList.sortedBy {
                            it.title.lowercase()
                        }

                        val itemsToRemove = mutableListOf<BookItem>()
                        val modifiedBookList = sortedList.toMutableList()
                        bookItemList.forEach { bookItem ->
                            if (bookItem.isFavourite) {
                                itemsToRemove.add(bookItem)
                            }
                        }

                        modifiedBookList.removeIf {
                            it.isFavourite
                        }

                        itemsToRemove.forEach {
                            modifiedBookList.add(0, it)
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
                        return bookItemList.sortedByDescending {
                            it.title.lowercase()
                        }
                    }

                    is BooksOrder.Hits -> {

                        val bookItemList = BookInfoMapper.mapBookInfoNetworkResponse(
                            bookList, favouriteList
                        )
                        return bookItemList.sortedByDescending {
                            it.hits
                        }
                    }

                    is BooksOrder.Favs -> {

                        val bookItemList = BookInfoMapper.mapBookInfoNetworkResponse(
                            bookList, favouriteList
                        )

                        val sortedList = bookItemList.sortedBy {
                            it.title.lowercase()
                        }

                        val itemsToRemove = mutableListOf<BookItem>()
                        val modifiedBookList = sortedList.toMutableList()
                        bookItemList.forEach { bookItem ->
                            if (bookItem.isFavourite) {
                                itemsToRemove.add(bookItem)
                            }
                        }

                        modifiedBookList.removeIf {
                            it.isFavourite
                        }

                        itemsToRemove.forEach {
                            modifiedBookList.add(it)
                        }

                        return modifiedBookList
                    }
                }
            }
        }
    }

    fun sortBookListBasedOnOrder(order: BooksOrder): List<BookItem> {
        return mapBookInfoAndFavourites(favouritesList, bookList, order)
    }

    fun addToFavourites(bookItem: BookItem, position: Int) {
        viewModelScope.launch {
            val result = authRepositoryImpl.addToFavourites(
                userUid ?: EMPTY_STRING,
                bookItem.id
            )
            if (result.isEmpty()) {
                _addFavouriteFlow.value = AddFavouriteUIState.AddFavouriteErrorState
            } else {
                favouritesList = result
                _addFavouriteFlow.value =
                    AddFavouriteUIState.AddFavouriteSuccessState(bookItem, position)
            }
        }
    }

    fun removeFromFavourites(bookItem: BookItem, position: Int) {
        viewModelScope.launch {
            val result = authRepositoryImpl.removeFromFavourites(
                userUid ?: EMPTY_STRING,
                bookItem.id
            )
            if (result.isEmpty()) {
                _removeFavouriteFlow.value = RemoveFavouriteUIState.RemoveFavouriteErrorState
            } else {
                favouritesList = result
                _removeFavouriteFlow.value =
                    RemoveFavouriteUIState.RemoveFavouriteSuccessState(bookItem, position)
            }
        }
    }

    fun convertTimeToDate(value: Int?): String {
        value?.let {
            val date = Date(value.toLong() * 1000)
            val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            var formattedDate = dateFormat.format(date)
            val day = SimpleDateFormat("d").format(date).toInt()

            formattedDate = formattedDate.replaceRange(0, 2, "")
            formattedDate = when (day) {
                1, 21, 31 -> "${day}st $formattedDate"
                2, 22 -> "${day}nd $formattedDate"
                3, 23 -> "${day}rd $formattedDate"
                else -> "${day}th $formattedDate"
            }
            return formattedDate

        }
        return "Invalid date format"
    }
}