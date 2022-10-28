package com.twx.marryfriend.bean.recommend

import org.json.JSONArray

data class HeadfaceBean(
    var content: String? = "",
    var create_time: String? = "",
    var file_name: String? = "",
    var file_type: String? = "",
    var id: Int? = 0,
    var image_height: String? = "",
    var image_url: String? = "",
    var image_width: String? = "",
    var real_status: Int? = 0,
    var status: Int? = 0,
    var user_id: Int? = 0,
)

data class Base(
    val age: Int? = null,
    val birthday: String? = null,
    val create_time: String? = null,
    val daily_hobbies: String? = null,
    val education: Int? = null,
    val height: Int? = null,
    val hometown: String? = null,
    val hometown_city_num: String? = null,
    val hometown_city_str: String? = null,
    val hometown_index: String? = null,
    val hometown_province_num: String? = null,
    val hometown_province_str: String? = null,
    val id: Int? = null,
    val industry_num: String? = null,
    val industry_str: String? = null,
    val introduce_self: String? = null,
    val introduce_status:Int=0,
    val marry_had: Int? = null,
    val nick: String? = null,
    val occupation_index: String? = null,
    val occupation_num: String? = null,
    val occupation_str: String? = null,
    val salary_range: Int? = null,
    val school_name: String? = null,
    val ta_in_my_mind: String? = null,
    val update_time: String? = null,
    val user_id: Int? = null,
    val user_sex: Int? = null,
    val work_city_num: String? = null,
    val work_city_str: String? = null,
    val work_index: String? = null,
    val work_province_num: String? = null,
    val work_province_str: String? = null,
) {
    var like_uid: Int? = null//我是否喜欢他
    var super_uid: Int? = null
    var focus_uid: Int? = null
    var ta_like_wo: Int? = null
}

data class Demand(
    val age_max: Int? = null,
    val age_min: Int? = null,
    val buy_car: Int? = null,
    val buy_house: Int? = null,
    val child_had: String? = null,//int数组
    val create_time: String? = null,
    val drink_wine: Int? = null,
    val education: String? = null,//int数组
    val figure_nan: String? = null,
    val figure_nv: Int? = null,
    val id: Int? = null,
    val industry_num: Int? = null,
    val industry_str: String? = null,
    val is_headface: Int? = null,
    val is_smoking: Int? = null,
    val marry_status: String? = null,//int数组
    val marry_time: Int? = null,
    val max_high: String? = null,
    val min_high: String? = null,
    val salary_range: String? = null,
    val update_time: String? = null,
    val user_id: Int? = null,
    val user_sex: Int? = null,
    val want_child: Int? = null,
    val work_place_code: String? = null,
    val work_place_str: String? = null,
    val work_province_code: String? = null,
    val work_province_str: String? = null,
) {
    fun getChild_hadArray(): List<Int> {
        if (child_had.isNullOrBlank()) {
            return emptyList()
        }
        val ja = JSONArray(child_had)
        if (ja.length() == 0) {
            return emptyList()
        } else {
            val resultArrayList = ArrayList<Int>()
            for (i in 0 until ja.length()) {
                resultArrayList.add(ja.getInt(i))
            }
            return resultArrayList
        }
    }

    fun getEducationArray(): List<Int> {
        if (education.isNullOrBlank()) {
            return emptyList()
        }
        val ja = JSONArray(education)
        if (ja.length() == 0) {
            return emptyList()
        } else {
            val resultArrayList = ArrayList<Int>()
            for (i in 0 until ja.length()) {
                resultArrayList.add(ja.getInt(i))
            }
            return resultArrayList
        }
    }

    fun getMarry_statusArray(): List<Int> {
        if (marry_status.isNullOrBlank()) {
            return emptyList()
        }
        val ja = JSONArray(marry_status)
        if (ja.length() == 0) {
            return emptyList()
        } else {
            val resultArrayList = ArrayList<Int>()
            for (i in 0 until ja.length()) {
                resultArrayList.add(ja.getInt(i))
            }
            return resultArrayList
        }
    }

    fun getSalary_rangeArray(): List<Int> {
        if (salary_range.isNullOrBlank()) {
            return emptyList()
        }
        val ja = JSONArray(salary_range)
        if (ja.length() == 0) {
            return emptyList()
        } else {
            val resultArrayList = ArrayList<Int>()
            for (i in 0 until ja.length()) {
                resultArrayList.add(ja.getInt(i))
            }
            return resultArrayList
        }
    }
}

data class More(
    val blood_type: String? = null,
    val buy_car: Int? = null,
    val buy_house: Int? = null,
    val child_had: Int? = null,
    val constellation: String? = null,
    val create_time: String? = null,
    val figure_nan: String? = null,
    val figure_nv: Int? = null,
    val id: Int? = null,
    val is_drinking: Int? = null,
    val is_smoking: Int? = null,
    val love_target: Int? = null,
    val marry_time: Int? = null,
    val nationality: String? = null,
    val target_show: Int? = null,
    val update_time: String? = null,
    val user_id: Int? = null,
    val user_sex: Int? = null,
    val want_child: Int? = null,
    val weight: Int? = null,
)

data class Photo(
    val content: String? = null,
    val create_time: String? = null,
    val file_name: String? = null,
    val file_type: String? = null,
    val id: Int? = null,
    val image_height: String? = null,
    val image_url: String? = null,
    val image_width: String? = null,
    val kind: Int? = null,
    val status: Int? = null,
    val user_id: Int? = null,
)

data class Verify(
    val identity_name: String? = null,
    val identity_number: String? = null,
    val identity_status: Int? = null,
)

data class VipInfo(
    val application: Int? = null,
    val begin_time: String? = null,
    val close_time: String? = null,
    val create_time: String? = null,
    val id: Int? = null,
    val level: Int? = null,
    val status: Int? = null,
    val update_time: String? = null,
    val user_id: Int? = null,
)

data class Zhaohu(
    val create_time: String? = null,
    val finish_proportion: Int? = null,
    val id: Int? = null,
    val user_id: Int? = null,
    val user_sex: Int? = null,
    val voice_long: String? = null,
    val voice_name: String? = null,
    val voice_status: Int? = null,
    val voice_time: String? = null,
    val voice_url: String? = null,
    val zhaohuyu_content: String? = null,
    val zhaohuyu_status: Any? = null,
    val zhaohuyu_time: Any? = null,
)

data class Place(
    val address: String? = null,
    val create_time: String? = null,
    val id: Int? = null,
    val jingdu: String? = null,
    val update_time: String? = null,
    val user_id: Int? = null,
    val weidu: String? = null,
)

data class Trends(
    var audit_status: Int? = null,
    var create_time: String? = null,
    var discuss_count: Int? = null,
    var id: Int? = null,
    var image_url: String? = null,
    var jingdu: String? = null,
    var label: String? = null,
    var like_count: Int? = null,
    var position: String? = null,
    var text_content: String? = null,
    var user_id: String? = null,
    var video_cover: String? = null,
    var video_url: String? = null,
    var weidu: String? = null,
)