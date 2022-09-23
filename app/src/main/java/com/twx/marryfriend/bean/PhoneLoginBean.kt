package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/11
 */
data class PhoneLoginBean(
    val code: String,
    val data: PhoneLoginData,
    val msg: String,
)

data class PhoneLoginData(
    val age: Int,
    val close_time_high: String,
    val close_time_low: String,
    val create_time: String,
    val jinbi_goldcoin: Int,
    val kind_type: Int,
    val level_high: Int,
    val level_low: Int,
    val nick: String,
    val server_time: String,
    val sex: Int,
    val user_id: Int,
    val user_mobile: String,
)