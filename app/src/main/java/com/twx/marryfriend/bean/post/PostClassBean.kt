package com.twx.marryfriend.bean.post

/**
 * 岗位
 */
data class PostClassBean(
    var code: Int? = 0,
    var `data`: List<OccupationDataBean>? = listOf(),
    var msg: String? = ""
)