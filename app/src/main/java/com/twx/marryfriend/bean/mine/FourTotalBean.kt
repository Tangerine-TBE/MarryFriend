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
    val focus: Int,
    val like: Int,
    val see: Int,
    val server: String,
)