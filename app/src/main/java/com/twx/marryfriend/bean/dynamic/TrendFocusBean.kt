package com.twx.marryfriend.bean.dynamic

/**
 * @author: Administrator
 * @date: 2022/7/4
 */
data class TrendFocusBean(
    val code: Int,
    val data: TrendFocusData,
    val msg: String,
)

data class TrendFocusData(
    val list: List<TrendFocusList>,
    val total: Int,
)

data class TrendFocusList(
    val age: Int,
    val audit_status: Int,
    val birthday: String,
    val close_time: String,
    val create_time: String,
    val discuss_count: Int,
    val education: Int,
    val focous_uid: Any,
    val guest_uid: Int?,
    val headface: String,
    val height: Int,
    val id: Int,
    val identity_status: Int,
    val image_url: String,
    val industry_str: String,
    val jingdu: String,
    val label: String,
    val like_count: Int,
    val nick: String,
    val occupation_str: String,
    val position: String,
    val text_content: String,
    val trends_type: Int,
    val user_id: String,
    val user_sex: Int,
    val video_cover: String,
    val video_url: String,
    val vip_level: Int,
    val weidu: String,
    val work_city_str: String,
)