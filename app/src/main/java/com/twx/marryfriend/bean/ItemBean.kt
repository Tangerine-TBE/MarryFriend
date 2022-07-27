package com.twx.marryfriend.bean

data class ItemBean(
    var title: String = "",
    var icon: Int = 0,
) {
    constructor() : this("", 0)
}