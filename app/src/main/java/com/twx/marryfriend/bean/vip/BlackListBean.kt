package com.twx.marryfriend.bean.vip

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
data class BlackListBean(
    val code: Int,
    val `data`: List<BlackListData>,
    val msg: String,
)

data class BlackListData(
    val create_time: String,
    val guest_uid: Int,
    val image_url: String,
    val nick: String,
)