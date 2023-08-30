package com.example.bookshelfapp.data.utils

sealed class BooksOrder(var orderType: OrderType) {
    class Title(orderType: OrderType): BooksOrder(orderType)
    class Hits(orderType: OrderType): BooksOrder(orderType)
    class Favs(orderType: OrderType): BooksOrder(orderType)
}
