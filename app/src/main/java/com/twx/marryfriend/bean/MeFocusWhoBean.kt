package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/24
 */
data class MeFocusWhoBean(
    val code: Int,
    val `data`: MeFocusWhoData,
    val msg: String,
)

data class MeFocusWhoData(
    val list: List<MeFocusWhoList>,
    val total: Int,
)

data class MeFocusWhoList(
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
)