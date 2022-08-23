package com.twx.marryfriend.bean.message

data class FriendInfo(
    var age: Int? = null,
    var birthday: String? = null,
    var create_time: String? = null,
    var daily_hobbies: String? = null,
    var education: Int? = null,
    var height: Int? = null,
    var hometown: String? = null,
    var hometown_city_num: String? = null,
    var hometown_city_str: String? = null,
    var hometown_index: String? = null,
    var hometown_province_num: String? = null,
    var hometown_province_str: String? = null,
    var id: Int? = null,
    var identity_status: Int? = null,
    var image_url: String? = null,
    var industry_num: String? = null,
    var industry_str: String? = null,
    var introduce_self: String? = null,
    var level: Int? = null,
    var like_uid: Any? = null,
    var marry_had: Int? = null,
    var nick: String? = null,
    var occupation_index: String? = null,
    var occupation_num: String? = null,
    var occupation_str: String? = null,
    var salary_range: Int? = null,
    var school_name: String? = null,
    var super_uid: Int? = null,
    var ta_in_my_mind: String? = null,
    var update_time: String? = null,
    var user_id: Int? = null,
    var user_sex: Int? = null,
    var work_city_num: String? = null,
    var work_city_str: String? = null,
    var work_index: String? = null,
    var work_province_num: String? = null,
    var work_province_str: String? = null
){
    fun isRealName():Boolean{
        return identity_status==1
    }

    fun isLike():Boolean{
        return like_uid!=null
    }

    fun isFlower():Boolean{
        return super_uid!=null
    }
}