package com.twx.marryfriend.bean.mine

/**
 * @author: Administrator
 * @date: 2022/5/24
 */
data class FourTotalBean(
    val code: Int,
    val data: FourTotalData,
    val msg: String,
)

data class FourTotalData(
    val disc: Int,
    val discTime: String?,
    val focus: Int,
    val focusTime: String?,
    val like: Int,
    val likeTime: String?,
    val see: Int,
    val seeTime: String?,
    val server: String,
)