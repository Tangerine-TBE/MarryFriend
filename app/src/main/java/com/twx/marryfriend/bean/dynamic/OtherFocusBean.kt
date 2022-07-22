package com.twx.marryfriend.bean.dynamic

/**
 * @author: Administrator
 * @date: 2022/6/28
 */
data class OtherFocusBean(
    val code: Int,
    val `data`:OtherFocusData,
    val msg: String
)

data class OtherFocusData(
    val list: List<OtherFocusList>,
    val total: Int
)

data class OtherFocusList(
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
    val work_city_str: String
)