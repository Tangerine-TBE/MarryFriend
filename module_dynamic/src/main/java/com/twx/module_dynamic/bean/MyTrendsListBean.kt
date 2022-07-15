package com.twx.module_dynamic.bean

import java.io.Serializable

/**
 * @author: Administrator
 * @date: 2022/6/27
 *
 * 获取我的动态列表
 *
 */
data class MyTrendsListBean(
    val code: Int,
    val `data`: MyTrendsListData,
    val msg: String,
)

data class MyTrendsListData(
    val list: List<MyTrendsList>,
    val total: Total,
)

data class MyTrendsList(
    val audit_status: Int,
    val create_time: String,
    val discuss_count: Int,
    val id: Int,
    val image_url: String,
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
) : Serializable

data class Total(
    val all: Int,
    val image: Int,
    val video: Int,
    val wenzi: Int,
)
