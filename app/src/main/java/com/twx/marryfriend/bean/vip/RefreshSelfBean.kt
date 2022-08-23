package com.twx.marryfriend.bean.vip

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
data class RefreshSelfBean(
    val code: Int,
    val data: RefreshSelfData,
    val msg: String
)

data class  RefreshSelfData(
    val begin_time_high: String,
    val begin_time_low: String,
    val close_time_high: String,
    val close_time_low: String,
    val create_time: String,
    val id: Int,
    val jinbi_goldcoin: Int,
    val level_high: Int,
    val level_low: Int,
    val status: Int,
    val update_time_high: String,
    val update_time_jinbi: String,
    val update_time_low: String,
    val user_id: Int
)
