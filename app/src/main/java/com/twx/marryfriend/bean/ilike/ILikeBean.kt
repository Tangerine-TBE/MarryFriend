package com.twx.marryfriend.bean.ilike

data class ILikeBean(
    var code: Int? = 0,
    var `data`: ILikeData? = ILikeData(),
    var msg: String? = ""
)