package com.twx.module_dynamic.bean

/**
 * @author: Administrator
 * @date: 2022/6/28
 */
data class MyFocusBean(
    val code: Int,
    val `data`: MyFocusData,
    val msg: String,
)

data class MyFocusData(
    val list: List<MyFocusList>,
    val total: Int,
)

data class MyFocusList(
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