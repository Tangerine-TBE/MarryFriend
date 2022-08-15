package com.twx.marryfriend.bean.dynamic

import java.io.Serializable

/**
 * @author: Administrator
 * @date: 2022/6/27
 *
 * 获取其他人的动态列表
 *
 */
data class OtherTrendsListBean(
    val code: Int,
    val `data`: OtherTrendsListData,
    val msg: String,
)

data class OtherTrendsListData(
    val list: List<OtherTrendsList>,
    val total: Total,
)

data class OtherTrendsList(
    val audit_status: Int,
    val create_time: String,
    val discuss_count: Int,
    val id: Int,
    val image_url: String,
    val is_like: Int?,
    val jingdu: String,
    val label: String,
    val like_count: Int,
    val position: String,
    val text_content: String,
    val trends_type: Int,
    val user_id: String,
    val video_cover: String,
    val video_url: String,
    val weidu: String,
)

data class OtherTrendsListTotal(
    val all: Int,
    val image: Int,
    val video: Int,
    val wenzi: Int,
)