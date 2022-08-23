package com.twx.marryfriend.bean

data class WebUrlBean(
    var title: String = "",
    var url: String = "",
) {
    constructor() : this("", "")
}