package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
data class BanBean(
    val code: Int,
    val data: BanData,
    val msg: String,
)

data class BanData(
    val array_string: String,
)