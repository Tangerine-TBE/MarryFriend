package com.twx.marryfriend.bean.dynamic

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
    var content_two: String,
    var count_two: Int?,
    val host_uid: Int,
    val id: Int,
    var id_two: Int,
    var image_two: String,
    val img_one: String,
    val nick_one: String,
    var nick_two: String,
    val one_level_uid: Int,
    var pid_two: Int,
    val sex_one: Int,
    var sex_two: Int,
    var tid_two: Int,
    val time_one: String,
    var time_two: String,
    val trends_id: Int,
    var two_last_uid: Int,
)