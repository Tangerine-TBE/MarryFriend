package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/11
 */
data class PhoneLoginBean(
    val code: String,
    val data: PhoneLoginData,
    val msg: String,
)

data class PhoneLoginData(
    val age: Int,
    val blacklist_close_time: String,
    val blacklist_permanent: Int,
    val buy_car: Int,
    val buy_house: Int,
    val child_had: Int,
    val close_time_high: String,
    val close_time_low: String,
    val create_time: String,
    val daily_hobbies: String,
    val headface_count: List<HeadfaceCount>,
    val hometown_city_num: String,
    val hometown_province_num: String,
    val industry_num: Int,
    val introduce_self: String,
    val is_smoking: Int,
    val jinbi_goldcoin: Int,
    val kind_type: Int,
    val level_high: Int,
    val level_low: Int,
    val nick: String,
    val occupation_num: Int,
    val photos_count: List<PhotosCount>,
    val server_time: String,
    val sex: Int,
    val user_id: Int,
    val user_mobile: String,
    val verify_status: Int,
    val voice_url: String,
    val want_child: Int,
    val zhaohuyu_content: String,
)

data class HeadfaceCount(
    val content: String,
    val create_time: String,
    val file_name: String,
    val file_type: String,
    val id: Int,
    val image_height: String,
    val image_url: String,
    val image_width: String,
    val real_face: Int,
    val status: Int,
    val update_time: String,
    val user_id: Int,
)

data class PhotosCount(
    val content: String,
    val create_time: String,
    val file_name: String,
    val file_type: String,
    val id: Int,
    val image_height: String,
    val image_url: String,
    val image_width: String,
    val status: Int,
    val user_id: Int,
)

