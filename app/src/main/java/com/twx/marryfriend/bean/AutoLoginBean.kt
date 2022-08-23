package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/11
 */
data class AutoLoginBean(
    val code: String,
    val data: AutoLoginData,
    val msg: String,
)

data class AutoLoginData(
    val close_time_high: String,
    val close_time_low: String,
    val create_time: String,
    val jinbi_goldcoin: Int,
    val level_high: Int,
    val level_low: Int,
    val server_time: String,
    val user_id: Int,
    val user_mobile: String
)
