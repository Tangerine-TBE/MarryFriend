package com.twx.marryfriend.bean.mine

/**
 * @author: Administrator
 * @date: 2022/5/24
 */
data class WhoLikeMeBean(
    val code: Int,
    val `data`: WhoLikeMeData,
    val msg: String
)

data class WhoLikeMeData(
    val list: List<WhoLikeMeList>,
    val total: Int
)

data class WhoLikeMeList(
    val age: Int,
    val audit_status: Int,
    val close_time_high: String,
    val close_time_low: String,
    val create_time: String,
    val dianzan_time: String,
    val education: Int,
    val headface: String,
    val id: Int,
    val identity_status: Any,
    val image_url: String,
    val jingdu: String,
    val label: String,
    val level_high: Int,
    val level_low: Int,
    val nick: String,
    val occupation_str: String,
    val position: String,
    val text_content: String,
    val trends_type: Int,
    val user_id: String,
    val user_sex: Int,
    val video_cover: String,
    val video_url: String,
    val weidu: String,
    val work_city_str: String

)