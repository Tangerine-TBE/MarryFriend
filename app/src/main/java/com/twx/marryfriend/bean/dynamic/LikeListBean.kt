package com.twx.marryfriend.bean.dynamic

/**
 * @author: Administrator
 * @date: 2022/6/27
 */
data class LikeListBean(
    val code: Int,
    val data: LikeListData,
    val msg: String,
)

data class LikeListData(
    val list: List<LikeList>,
    val total: String,
)

data class LikeList(
    val age: Int,
    val birthday: String,
    val create_time: String,
    val delete_status: Int,
    val education: Int,
    val guest_uid: Int,
    val height: Int,
    val host_read: Int,
    val host_uid: Int,
    val id: Int,
    val image_url: String,
    val industry_str: String,
    val occupation_str: String,
    val nick: String,
    val num: Int,
    val trends_id: Int,
    val user_sex: Int,
    val work_city_str: String,
)