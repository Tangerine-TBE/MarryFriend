package com.twx.marryfriend.bean.mine

/**
 * @author: Administrator
 * @date: 2022/5/24
 */
data class WhoSeeMeBean(
    val code: Int,
    val data: WhoSeeMeData,
    val msg: String,
)

data class WhoSeeMeData(
    val list: List<WhoSeeMeList>,
    val total: Int,
    val server_time: String,
)

data class WhoSeeMeList(
    val age: Int,
    val count_total: Int,
    val create_time: String,
    val education: Int,
    val guest_uid: Int,
    val host_uid: Int,
    val id: Int,
    val identity_status: Any,
    val image_url: String,
    val level: Any,
    val nick: String?,
    val occupation_str: String,
    val update_time: String,
    val user_sex: Int,
    val work_city_str: String,

    val level_low: Int,
    val close_time_low: String,
    val level_high: Int,
    val close_time_high: String,

    val introduce_self: String,

    val real_face: Int,

    )