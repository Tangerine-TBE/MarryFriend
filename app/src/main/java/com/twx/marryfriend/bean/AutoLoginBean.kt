package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/11
 */
data class AutoLoginBean(
    val code: String,
    val data: AutoLoginData,
    val msg: String,
)

data class AutoLoginData(
    val age: Int,

    val blacklist_close_time: String,
    val blacklist_permanent: Int,
    val blacklist_status: Int,

    val buy_car: Int,
    val buy_house: Int,
    val child_had: Int,
    val close_time_high: String,
    val close_time_low: String,
    val create_time: String,
    val daily_hobbies: String,
    val headface_count: List<HeadfaceCount>,
    val hometown_city_num: Int,
    val hometown_city_str: String,
    val hometown_province_num: Int,
    val hometown_province_str: String,
    val industry_num: Int,
    val industry_str: String,
    val introduce_self: String,
    val is_smoking: Int,
    val jinbi_goldcoin: Int,
    val kind_type: Int,
    val level_high: Int,
    val level_low: Int,
    val nick: String,
    val occupation_num: Int,
    val occupation_str: String,
    val photos_count: List<PhotosCount>,
    val server_time: String,
    val sex: Int,
    val user_id: Int,
    val user_mobile: String,
    val voice_url: String,
    val want_child: Int,
    val zhaohuyu_content: String,

    val identity_name: String,
    val identity_number: String,
    val identity_status: Int,

    )
