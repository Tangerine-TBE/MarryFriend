package com.twx.marryfriend.bean.ilike

data class ILikeData(
    var list: List<ILikeItemBean>? = listOf(),
    var total: Int? = 0
)