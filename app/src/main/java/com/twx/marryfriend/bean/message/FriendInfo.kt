package com.twx.marryfriend.bean.message

import com.xyzz.myutils.textTimeToTimeInMillis

data class FriendInfo(
//    var id: Int? = null,
    var user_id: Int? = null,
    var user_sex: Int? = null,
    var nick: String? = null,
    var birthday: String? = null,
    var age: Int? = null,
    var height: Int? = null,
    var school_name: String? = null,
    var education: Int? = null,
    var industry_num: String? = null,
    var industry_str: String? = null,
    var occupation_num: String? = null,
    var occupation_str: String? = null,
    var occupation_index: String? = null,
    var work_province_num: String? = null,
    var work_province_str: String? = null,
    var work_city_num: String? = null,
    var work_city_str: String? = null,
    var work_index: String? = null,
    var hometown_province_num: String? = null,
    var hometown_province_str: String? = null,
    var hometown_city_num: String? = null,
    var hometown_city_str: String? = null,
    var hometown_index: String? = null,
    var salary_range: Int? = null,
    var marry_had: Int? = null,
    var introduce_self: String? = null,
    var daily_hobbies: String? = null,
    var ta_in_my_mind: String? = null,
    var update_time: String? = null,
    var create_time: String? = null,
    var super_uid: Any? = null,
    var image_url: String? = null,
    var woLikeTa: Any? = null,
    var taLikeWo: Any? = null,
    var level_low: Int? = null,
    var close_time_low: String? = null,
    var level_high: Int? = null,
    var close_time_high: String? = null,
    var identity_status: Int? = null
){
    var blacklist_close_time:String?=null
    var blacklist_permanent:Int?=null

    /**
     * 该账号是否被系统封
     */
    fun isSystemBlacklist():Boolean{
        if (blacklist_permanent==1){
            return true
        }else{
            return (blacklist_close_time?.textTimeToTimeInMillis()?:0L)>System.currentTimeMillis()
        }
    }

    fun isVip():Boolean{
        return (close_time_low?.textTimeToTimeInMillis()?:0L)>System.currentTimeMillis()
    }

    fun isSuperVip():Boolean{
        return (close_time_high?.textTimeToTimeInMillis()?:0L)>System.currentTimeMillis()
    }

    fun isRealName():Boolean{
        return identity_status==1
    }

    fun isMutualLike():Boolean{
        return woLikeTa!=null&&taLikeWo!=null
    }

    fun isFlower():Boolean{
        return super_uid!=null
    }
}