package com.twx.marryfriend.bean.likeme

data class LikeMeBean(
    var code: Int? = 0,
    var `data`: LikeMeData? = LikeMeData(),
    var msg: String? = ""
)