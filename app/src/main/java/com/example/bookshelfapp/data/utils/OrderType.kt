package com.example.bookshelfapp.data.utils

sealed class OrderType {
    object Ascending: OrderType()
    object Descending: OrderType()
}
