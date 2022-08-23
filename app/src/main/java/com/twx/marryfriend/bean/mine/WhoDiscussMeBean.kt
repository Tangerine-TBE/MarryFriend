package com.twx.marryfriend.bean.mine

/**
 * @author: Administrator
 * @date: 2022/5/24
 */
data class WhoDiscussMeBean(
    val code: Int,
    val data: WhoDiscussMeData,
    val msg: String,
)

data class WhoDiscussMeData(
    val list: List<DiscussList>,
    val total: Int,
)

data class DiscussList(
    val age: Int,
    val audit_status: Int,
    val birthday: String,
    val content: String,
    val create_time: String,
    val d_time: String,
    val d_u_id: Int,
    val headface: String,
    val id: Int,
    val image_url: String,
    val jingdu: String,
    val label: String,
    val nick: String,
    val position: String,
    val text_content: String,
    val trends_type: Int,
    val user_id: String,
    val user_sex: Int,
    val video_cover: String,
    val video_url: String,
    val weidu: String,
)