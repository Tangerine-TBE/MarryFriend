package com.twx.marryfriend.bean.set

data class MessageSwitchBean(
    var title: String = "",
    var subtitle: String = "",
    var switch: Boolean,
    val isDouble: Boolean,
) {
    constructor() : this("", "", false, false)
}


