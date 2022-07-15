package com.twx.module_base.bean

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
    val create_time: String,
    val server_time: String,
    val user_id: Int,
    val user_mobile: String,
    val user_vipExpire: String,
    val user_vipLevel: Int,
)