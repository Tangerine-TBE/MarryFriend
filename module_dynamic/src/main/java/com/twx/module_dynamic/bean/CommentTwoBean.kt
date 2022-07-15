package com.twx.module_dynamic.bean

/**
 * @author: Administrator
 * @date: 2022/6/28
 */
data class CommentTwoBean(
    val code: Int,
    val `data`: CommentTwoData,
    val msg: String,
)

data class CommentTwoData(
    val list: List<CommentTwoList>,
    val total: Int,
)

data class CommentTwoList(
    val content: String,
    val create_time: String,
    val first_img_url: String,
    val first_nick: String,
    val first_sex: Int,
    val host_read: Int,
    val host_uid: Int,
    val id: Int,
    val last_img_url: String,
    val last_nick: String,
    val last_sex: Int,
    val one_level_uid: Int,
    val one_read: Int,
    val pid: Int,
    val trends_id: Int,
    val two_first_uid: Int,
    val two_last_uid: Int,
    val two_read: Int,
)