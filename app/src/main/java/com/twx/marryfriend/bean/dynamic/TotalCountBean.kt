package com.twx.marryfriend.bean.dynamic

/**
 * @author: Administrator
 * @date: 2022/7/4
 */
data class TotalCountBean(
    val code: Int,
    val data: TotalCountData,
    val msg: String,
)

data class TotalCountData(
    val discuss: String,
    val like: Int,
)
