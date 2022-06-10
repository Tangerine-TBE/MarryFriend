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
    val create_time: String,
    val server_time: String,
    val user_id: Int,
    val user_mobile: String,
    val user_vipExpire: String,
    val user_vipLevel: Int,
)