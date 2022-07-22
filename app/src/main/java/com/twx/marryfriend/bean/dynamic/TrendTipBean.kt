package com.twx.marryfriend.bean.dynamic

/**
 * @author: Administrator
 * @date: 2022/7/4
 */
data class TrendTipBean(
    val code: Int,
    val data: TrendTipData,
    val msg: String
)

data class TrendTipData(
    val list: List<TrendTipList>,
    val total: Int
)

data class TrendTipList(
    val age: Int,
    val audit_status: Int,
    val create_time: String,
    val education: Int,
    val height: Int,
    val id: Int,
    val image_url: String,
    val jingdu: String,
    val label: String,
    val like_time: String,
    val like_uid: Int,
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
