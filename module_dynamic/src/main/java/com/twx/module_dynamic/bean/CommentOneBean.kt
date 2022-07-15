package com.twx.module_dynamic.bean

/**
 * @author: Administrator
 * @date: 2022/6/28
 */
data class CommentOneBean(
    val code: Int,
    val `data`: CommentOneData,
    val msg: String,
)

data class CommentOneData(
    val list: List<CommentOneList>,
    val total: Int,
)

data class CommentOneList(
    val content_one: String,
    val content_two: String,
    val count_two: Int?,
    val host_uid: Int,
    val id: Int,
    val id_two: Int,
    val image_two: String,
    val img_one: String,
    val nick_one: String,
    val nick_two: String,
    val one_level_uid: Int,
    val pid_two: Int,
    val sex_one: Int,
    val sex_two: Int,
    val tid_two: Int,
    val time_one: String,
    val time_two: String,
    val trends_id: Int,
    val two_last_uid: Int,
)