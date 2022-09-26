package com.twx.marryfriend.bean.message.mutual

import com.twx.marryfriend.bean.Sex

data class MutualLikeData(
    var age: Int? = null,
    var birthday: String? = null,
    var education: Int? = null,
    var identity_status: Int? = null,
    var image_url: String? = null,
    var industry_str: String? = null,
    var level: Int? = null,
    var nick: String? = null,
    var occupation_str: String? = null,
    var user_id: Int? = null,
    var user_sex: Int? = null,
    var work_city_str: String? = null,
    var work_province_str: String? = null
){
//    fun getAgeText():String{
//        return age?.let {
//            "${it}岁"
//        }?:""
//    }
    var ageText=age?.let {
        "${it}岁"
    }?:""

    fun getSex(): Sex {
        return Sex.toSex(user_sex)
    }
}