package com.example.bookshelfapp.presentation.bookshelf.placeholders

sealed class AddFavouriteUIState {

    data class AddFavouriteSuccessState(val bookItem: BookItem, val position: Int): AddFavouriteUIState()

    object AddFavouriteErrorState: AddFavouriteUIState()
}
