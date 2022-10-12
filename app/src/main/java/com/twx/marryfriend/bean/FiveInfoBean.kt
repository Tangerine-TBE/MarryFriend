package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/24
 */

data class FiveInfoBean(
    val code: Int,
    val data: FiveInfoData,
    val msg: String,
)

data class FiveInfoData(
    val base: Base,
    val demand: Demand,
    val headface: List<Headface>,
    val more: More,
    val photos: List<Photo>,
    val position: Position,
    val verify: Verify,
    val vip_info: VipInfo,
    val zhaohu: Zhaohu,
    val zo_place: List<Any>,
    val blacklist: Blacklist?,
)

data class Base(
    val age: Int,
    val birthday: String,
    val create_time: String,
    val daily_hobbies: String,
    val education: Int,
    val height: Int,
    val hometown_city_num: String,
    val hometown_city_str: String,
    val hometown_index: String,
    val hometown_province_num: String,
    val hometown_province_str: String,
    val id: Int,
    val industry_num: String,
    val industry_str: String,
    val introduce_self: String,
    val marry_had: Int,
    val nick: String,
    val occupation_index: String,
    val occupation_num: String,
    val occupation_str: String,
    val salary_range: Int,
    val school_name: String,
    val ta_in_my_mind: String,
    val update_time: String,
    val user_id: Int,
    val user_sex: Int,
    val work_city_num: String,
    val work_city_str: String,
    val work_index: String,
    val work_province_num: String,
    val work_province_str: String,
)

data class Demand(
    val age_max: Int,
    val age_min: Int,
    val buy_car: Int,
    val buy_house: Int,
    val child_had: String,
    val create_time: String,
    val drink_wine: Int,
    val education: String,
    val figure_nan: Int,
    val figure_nv: Int,
    val id: Int,
    val industry_num: Int,
    val industry_str: String,
    val is_headface: Int,
    val is_smoking: Int,
    val marry_status: String,
    val marry_time: Int,
    val max_high: Int,
    val min_high: Int,
    val salary_range: String,
    val update_time: String,
    val user_id: Int,
    val want_child: Int,
)

data class Headface(
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

data class More(
    val blood_type: String,
    val buy_car: Int,
    val buy_house: Int,
    val child_had: Int,
    val constellation: String,
    val create_time: String,
    val figure_nan: String,
    val figure_nv: Int,
    val id: Int,
    val is_drinking: Int,
    val is_smoking: Int,
    val love_target: Int,
    val marry_time: Int,
    val nationality: String,
    val target_show: Int,
    val update_time: String,
    val user_id: Int,
    val want_child: Int,
    val weight: Int,
)

data class Photo(
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

data class Position(
    val address: String,
    val create_time: String,
    val id: Int,
    val jingdu: String,
    val update_time: String,
    val user_id: Int,
    val weidu: String,
)

data class Verify(
    val create_time: String,
    val id: Int,
    val identity_img_url: String,
    val identity_name: String,
    val identity_number: String,
    val identity_status: Int,
    val identity_time: Any,
    val user_id: Int,
)

data class VipInfo(
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
    val user_id: Int,
)

data class Zhaohu(
    val create_time: String,
    val finish_proportion: Int,
    val id: Int,
    val user_id: Int,
    val user_sex: Int,
    val voice_long: String,
    val voice_name: String,
    val voice_status: Int,
    val voice_time: String,
    val voice_url: String,
    val zhaohuyu_content: String,
    val zhaohuyu_status: Int,
    val zhaohuyu_time: String,
)


data class Blacklist(
    val id: Int,
    val user_id: Int,
    val blacklist_start_time: String,
    val blacklist_close_time: String,
    val time_type: Int,
    val reason_code: Int,
    val reason_text: String,
    val blacklist_permanent: Int,
    val blacklist_status: Int,
    val create_time: String,
    val create_user: Int,
    val update_time: Any,
    val update_user: Int,
)



