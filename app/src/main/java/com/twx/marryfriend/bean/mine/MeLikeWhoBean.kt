package com.twx.marryfriend.bean.mine

/**
 * @author: Administrator
 * @date: 2022/5/24
 */
data class MeLikeWhoBean(
    val code: Int,
    val `data`: MeLikeWhoData,
    val msg: String
)

data class MeLikeWhoData(
    val list: List<WhoLikeMeList>,
    val total: Int,
    val server_time: String,
)

data class MeLikeWhoList(
    val age: Int,
    val audit_status: Int,
    val create_time: String,
    val dianzan_time: String,
    val education: Int,
    val headface: String,
    val id: Int,
    val identity_status: Any,
    val image_url: String,
    val jingdu: String,
    val label: String,
    val level: Any,
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
    val work_city_str: String,

    val level_low: Int,
    val close_time_low: String,
    val level_high: Int,
    val close_time_high: String,

)
