package com.example.bookshelfapp.presentation.bookshelf.placeholders

sealed class RemoveFavouriteUIState {
    data class RemoveFavouriteSuccessState(val bookItem: BookItem, val position: Int) :
        RemoveFavouriteUIState()

    object RemoveFavouriteErrorState : RemoveFavouriteUIState()
}
