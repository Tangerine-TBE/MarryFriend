package com.twx.marryfriend.bean.dynamic

/**
 * @author: Administrator
 * @date: 2022/6/28
 */

data class CommentOneCreateBean(
    val code: Int,
    val `data`: CommentOneCreateData,
    val msg: String
)

data class CommentOneCreateData(
    val one_id: Int,
    val server_time: String
)