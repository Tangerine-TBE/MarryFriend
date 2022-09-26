package com.twx.marryfriend.bean.message.mutual

data class MutualLikeBean(
    var list: List<MutualLikeData>? = listOf(),
    var total: Int? = 0
)