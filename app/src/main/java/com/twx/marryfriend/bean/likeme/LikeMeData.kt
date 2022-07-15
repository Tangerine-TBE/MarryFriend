package com.twx.marryfriend.bean.likeme

data class LikeMeData(
    var list: List<LikeMeItemBean>? = listOf(),
    var total: Int? = 0
)