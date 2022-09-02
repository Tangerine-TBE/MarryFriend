package com.twx.marryfriend.bean.mine

/**
 * @author: Administrator
 * @date: 2022/5/24
 */
data class WhoFocusMeBean(
    val code: Int,
    val `data`: WhoFocusMeData,
    val msg: String,
)

data class WhoFocusMeData(
    val list: List<WhoFocusMeList>,
    val total: Int,
    val server_time: String,
)

data class WhoFocusMeList(
    val age: Int,
    val create_time: String,
    val education: Int,
    val guest_uid: Int,
    val host_uid: Int,
    val id: Int,
    val identity_status: Any,
    val image_url: String,
    val level: Any,
    val nick: String,
    val occupation_str: String,
    val user_sex: Int,
    val work_city_str: String,

    val level_low: Int,
    val close_time_low: String,
    val level_high: Int,
    val close_time_high: String,

    val introduce_self: String,
    val hometown_province_str: String,
    val salary_range: String,

    )