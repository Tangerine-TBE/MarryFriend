package com.twx.marryfriend.bean.vip

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
data class CoinRecordBean(
    val code: Int,
    val data: CoinRecordData,
    val msg: String,
)

data class CoinRecordData(
    val list: List<CoinRecordList>,
    val total: Int,
)

data class CoinRecordList(
    val create_time: String,
    val id: Int,
    val jinbi_multiple: Int,
    val jinbi_now_new: Int,
    val jinbi_operation: Int,
    val jinbi_original: Int,
    val jinbi_type: Int,
    val projiect_kind: Int,
    val receive_nick: String,
    val receive_uid: Int,
    val user_id: Int,
)