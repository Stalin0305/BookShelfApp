package com.example.bookshelfapp.data.utils

sealed class BooksOrder(val orderType: OrderType) {
    class Title(orderType: OrderType): BooksOrder(orderType)
    class Hits(orderType: OrderType): BooksOrder(orderType)
    class Favs(orderType: OrderType): BooksOrder(orderType)
}
