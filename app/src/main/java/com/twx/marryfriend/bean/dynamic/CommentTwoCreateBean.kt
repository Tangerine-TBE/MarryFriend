package com.twx.marryfriend.bean.dynamic

/**
 * @author: Administrator
 * @date: 2022/6/28
 */

data class CommentTwoCreateBean(
    val code: Int,
    val `data`: CommentTwoCreateData,
    val msg: String
)

data class CommentTwoCreateData(
    val one_id: Int,
    val server_time: String
)