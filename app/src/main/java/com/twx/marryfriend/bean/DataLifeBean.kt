package com.twx.marryfriend.bean

data class DataLifeBean(
    var ImageUrl: String = "",
    var ImageState: String = "",
) {
    constructor() : this("", "")
}