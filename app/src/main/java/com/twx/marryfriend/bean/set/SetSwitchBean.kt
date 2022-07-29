package com.twx.marryfriend.bean.set

data class SetSwitchBean(
    var title: String = "",
    var icon: Int = 0,
    var switch: Boolean,
) {
    constructor() : this("", 0, false)
}


