package com.example.bookshelfapp.presentation.bookshelf.placeholders

sealed class BookListUiState {
    object BookListUILoadingState : BookListUiState()

    object BookListUIErrorState : BookListUiState()

    data class BookListUISuccessState(val bookList: List<BookItem>) : BookListUiState()

}
