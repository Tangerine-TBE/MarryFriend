package com.twx.marryfriend.bean.vip

/**
 * @author: Administrator
 * @date: 2022/5/13
 */

data class AliPayBean(
    val code: String,
    val data: AliPayData,
    val msg: String
)

data class AliPayData(
    val str: String
)