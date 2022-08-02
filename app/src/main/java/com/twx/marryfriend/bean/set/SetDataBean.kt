package com.twx.marryfriend.bean.set

data class SetDataBean(
    var title: String = "",
    var icon: Int = 0,
    val data: String,
) {
    constructor() : this("", 0, "")
}


