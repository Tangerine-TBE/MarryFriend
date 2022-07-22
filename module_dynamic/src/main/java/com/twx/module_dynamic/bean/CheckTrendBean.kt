package com.twx.module_dynamic.bean

/**
 * @author: Administrator
 * @date: 2022/6/27
 */
data class CheckTrendBean(
    val code: Int,
    val data: CheckTrendData,
    val msg: String,
)

data class CheckTrendData(
    val imgs: List<CheckTrendImg>,
    val list: List<CheckTrendList>,
)

data class CheckTrendImg(
    val image_url: String,
    val user_id: Int,
)

data class CheckTrendList(
    val age: Int,
    val audit_status: Int,
    val birthday: String,
    val close_time: String,
    val create_time: String,
    val education: Int,
    val headface: String,
    val height: Int,
    val id: Int,
    val identity_status: Int,
    val image_url: String,
    val industry_str: String,
    val jingdu: String,
    val label: String,
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
    val like_count: Int?,
    val discuss_count: Int?,
    val focous_uid: Int?,
    val guest_uid: Int?,
)