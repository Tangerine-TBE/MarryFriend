package com.twx.marryfriend.bean.search

import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.Label
import com.twx.marryfriend.bean.recommend.RecommendBean

data class SearchResultItem(
    var age: Int? = 0,
    var close_time: String? = "",
    var education: Int? = 0,
    var height: Int? = 0,
    var hometown_city_str: String? = "",
    var identity_name: String? = "",
    var identity_status: Int? = 0,
    private var image_url: String? = "",
    var img_count: Int? = 0,
    var introduce_self: String? = "",
    var like_uid: Int? = 0,
    var marry_had: Int? = 0,
    var nick: String? = "",
    var occupation_str: String? = "",
    var ted_count: Int? = 0,
    var user_id: Int? = 0,
    var user_sex: Int? = 0,
    var vip_level: Int = 0,
    var img_status:Int=0,
    var work_city_str: String? = ""
){
    fun getHeadImage():String?{
        return if (isRealImage()){
            image_url
        }else{
            UserInfo.getDefHeadImage(user_sex?:UserInfo.reversalSex(UserInfo.getUserSex()))
        }
    }
    fun isRealImage():Boolean{
        return img_status==1
    }

    fun isLike():Boolean=like_uid!=null

    fun like(){
        like_uid=user_id
    }

    fun isRealName():Boolean{
        return identity_status==1
    }
    fun isVip():Boolean{
        return vip_level>0
    }

    fun getAgeDemand(): Label?{
        return "${age}岁".toLabel(R.mipmap.ic_label_age)
    }
    fun getEducationStr():Label?{
        return RecommendBean.getEducationStr(education)
    }

    /**
     * 工作城市
     */
    fun getCurrentResidence(): Label?{
//        return findIndustry(base?.work_city_num?.toIntOrNull()?:0)
        return (work_city_str)?.toLabel(R.mipmap.ic_label_residence)
    }

    fun getHeight(): Label?{
        return ((height?:return null).toString()+"cm").toLabel(R.mipmap.ic_label_height)
    }
    fun getHometown(): Label?{
        return hometown_city_str?.toLabel(R.mipmap.ic_label_hometown)
    }

    fun getLabels():List<Label>{
        val arrayList=ArrayList<Label>()
        getAgeDemand()?.also {
            arrayList.add(it)
        }
        getEducationStr()?.also {
            arrayList.add(it)
        }
        getCurrentResidence()?.also {
            arrayList.add(it)
        }
        getHeight()?.also {
            arrayList.add(it)
        }
        getHometown()?.also {
            arrayList.add(it)
        }
        return arrayList
    }

    private fun String.toLabel(icon: Int): Label?{
        return if (this.isBlank()) {
            null
        }else{
            Label(icon,this)
        }
    }
}