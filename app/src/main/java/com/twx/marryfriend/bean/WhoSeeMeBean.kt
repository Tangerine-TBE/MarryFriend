package com.twx.marryfriend.bean

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
    val nick: String,
    val occupation_str: String,
    val update_time: String,
    val user_sex: Int,
    val work_city_str: String,
)