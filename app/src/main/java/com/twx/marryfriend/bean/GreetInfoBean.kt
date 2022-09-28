package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/24
 */
data class GreetInfoBean(
    val code: Int,
    val data: GreetInfoData,
    val msg: String,
)

data class GreetInfoData(
    val create_time: String,
    val id: Int,
    val user_id: Int,
    val user_sex: Int,
    val voice_long: String,
    val voice_status: Int,
    val voice_time: Any,
    val voice_url: String,
    val zhaohuyu_content: String,
    val zhaohuyu_status: Any,
    val zhaohuyu_time: Any,
)