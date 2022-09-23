package com.twx.marryfriend.bean.set

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
data class DeleteBlackListBean(
    val code: Int,
    val data: List<DeleteBlackListData>,
    val msg: String,
)

data class DeleteBlackListData(
    val image_url: String,
    val nick: String,
)