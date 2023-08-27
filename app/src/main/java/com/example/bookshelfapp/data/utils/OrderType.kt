package com.example.bookshelfapp.data.utils

sealed class OrderType {
    data object Ascending: OrderType()
    data object Descending: OrderType()
}
