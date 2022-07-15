package com.twx.marryfriend.bean.search

data class Data(
    var list: List<SearchResultItem>? = listOf(),
    var total: Int? = 0
)