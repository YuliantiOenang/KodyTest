package com.yulianti.kodytest.data.model

data class PaginatedResult<T>(
    val items: List<T>,
    val totalSize: Int,
    val offset: Int,
    val count: Int
)