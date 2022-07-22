package com.twx.marryfriend.bean

import com.twx.marryfriend.R
import java.lang.IllegalStateException
import java.text.NumberFormat


data class RecommendBean(
    val base: Base?=null,
    val demand: Demand?=null,
    val more: More?=null,
    val photos: List<Photo>?=null,
    val place: Place?=null,
    val trends: List<Trends>?=null,//动态
    val trends_total: Int=0,
    val verify: Verify?=null,
    val vip_info: VipInfo?=null,
    val zhaohu: Zhaohu?=null
){
    fun getId():Int{
        return base?.id?:throw IllegalStateException("id为空")
    }

    /**
     * 头像
     */
    fun getHeadImg():String?{
        return photos?.find { it.kind==1 }?.image_url
    }
    fun isHeadIdentification():Boolean{
        return photos?.find { it.kind==1 }?.status==1
    }
    
    fun getLongitude():Double?{
        return place?.jingdu?.toDoubleOrNull()
    }
    fun getLatitude():Double?{
        return place?.weidu?.toDoubleOrNull()
    }
    fun getNickname():String{
        return base?.nick?:""
    }
    fun getAge():Int{
        return base?.age?:0
    }
    fun isRealName():Boolean{
        return verify?.identity_status==1
    }
    fun getRealNameNumber():String?{
        return verify?.identity_number
    }
    fun getOccupation():String{
        return base?.industry_str?:""
    }
    fun getSchoolName():String{
        return base?.school_name?:""
    }
    fun getDynamicCount():Int{
        return trends_total
    }
    fun getDynamic()=trends?: emptyList()
    fun isVip():Boolean{
        return (vip_info?.level?:0)>0
    }
    fun getAboutMeLife():String{
        return base?.introduce_self?:""
    }
    fun getAboutMeWork():String{
        return base?.daily_hobbies?:""
    }
    fun getAboutMeHobby():String{
        return base?.ta_in_my_mind?:""
    }
    fun getAboutMeThreeOutlooks():String{
        return base?.ta_in_my_mind?:""
    }
    fun getAboutMePhoto():String?{
        return photos?.find { it.kind==2 }?.image_url
    }
    fun getAlbumPhoto():List<String>{
        return photos?.filter { it.kind == 3 }?.mapNotNull { it.image_url }?.toList()?: emptyList()
    }

    fun getUserSex():Sex{
        return Sex.toSex(base?.user_sex)
    }

    fun getVoiceUrl():String?{
        return zhaohu?.voice_url
    }

    fun getVoiceDurationStr():String{
        val duration=zhaohu?.voice_long?.toIntOrNull()?:0
        val numberFormat=NumberFormat.getNumberInstance().also {
            it.minimumIntegerDigits=2
        }
        return "${numberFormat.format(duration/60)}:${numberFormat.format(duration%60)}"
    }

    companion object{
        private fun String.toLabel(icon: Int): Label?{
            return if (this.isBlank()) {
                null
            }else{
                Label(icon,this)
            }
        }
        /**
         * 0没填写, 1大专以下,2大专，3本科，4硕士，5博士,6博士以上
         */
        fun getEducationStr(education:Int?): Label?{
            return when(education){
                0->{
                    null
                }
                1->{
                    "大专以下"
                }
                2->{
                    "大专"
                }
                3->{
                    "本科"
                }
                4->{
                    "硕士"
                }
                5->{
                    "博士"
                }
                6->{
                    "博士以上"
                }
                else-> null
            }?.toLabel(R.mipmap.ic_label_school)
        }
        fun getMarry_hadStr(marry_status:Int?): Label?{
            return when(marry_status){
                0->{
                    null//"不限"
                }
                1->{
                    "未婚"
                }
                2->{
                    "离异"
                }
                3->{
                    "丧偶"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_marriage)
        }
        fun getChild_hadStr(child_had:Int?): Label?{
            return when(child_had){
                0->{
                    null//"不限"
                }
                1->{
                    "没生过娃"
                }
                2->{
                    "有娃住一起"
                }
                3->{
                    "有娃偶尔在一起"
                }
                4->{
                    "有娃别人带着的"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_children)
        }
        fun getWant_childStr(want_child:Int?): Label?{
            return when(want_child){
                0->{
                    null//"不限"
                }
                1->{
                    "视情况而定"
                }
                2->{
                    "想要孩子"
                }
                3->{
                    "不想要孩子"
                }
                4->{
                    "以后再告诉你"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_children)
        }
        fun getIs_smoking(is_smoking:Int?): Label?{
            return when(is_smoking){
                0->{
                    null//"不限"
                }
                1->{
                    "可以随意抽烟"
                }
                2->{
                    "偶尔抽烟"
                }
                3->{
                    "禁止抽烟"
                }
                4->{
                    "社交场合可以抽"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_smoking)
        }
        fun getDrink_wine(drink_wine:Int?): Label?{
            return when(drink_wine){
                0->{
                    null//"不限"
                }
                1->{
                    "可以随意喝酒"
                }
                2->{
                    "偶尔喝酒"
                }
                3->{
                    "禁止喝酒"
                }
                4->{
                    "社交场合可以喝"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_drink)
        }
        fun getIs_headface(is_headface:Int?): Label?{
            return when(is_headface){
                0->{
                    null//"不限"
                }
                1->{
                    "需要头像"
                }
                2->{
                    "不用头像"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_hometown)
        }
        fun getSalary_range(salary_range:Int?): Label?{
            return when(salary_range){
                0->{
                    null//"不限"
                }
                1->{
                    "五千及以下"
                }
                2->{
                    "五千一万"
                }
                3->{
                    "一万两万"
                }
                4->{
                    "两万~四万"
                }
                5->{
                    "四万到七万"
                }
                6->{
                    "七万及以上"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_income)
        }
        fun getFigure_nan(figure_nan:Int?): Label?{
            return when(figure_nan){
                1->{
                    "一般"
                }
                2->{
                    "瘦长"
                }
                3->{
                    "运动员型"
                }
                4->{
                    "比较魁梧"
                }
                5->{
                    "壮实"
                }
                6->{
                    null//"不限"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_figure)
        }
        fun getFigure_nv(figure_nv:Int?): Label?{
            return when(figure_nv){
                1->{
                    "一般"
                }
                2->{
                    "瘦长"
                }
                3->{
                    "苗条"
                }
                4->{
                    "高大美丽"
                }
                5->{
                    "丰满"
                }
                6->{
                    "富线条美"
                }
                0->{
                    null//"不限"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_figure)
        }
        fun getMarry_time(marry_time:Int?): Label?{
            return when(marry_time){
                0->{
                    null//"不限"
                }
                1->{
                    "半年"
                }
                2->{
                    "一年"
                }
                3->{
                    "两年"
                }
                4->{
                    "恋爱后再考虑"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_card)
        }
        fun getBuy_car(buy_car:Int?): Label?{
            return when(buy_car){
                0->{
                    null//"不限"
                }
                1->{
                    "买了"
                }
                2->{
                    "没买"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_vehicle)
        }
        fun getBuy_house(buy_house:Int?): Label?{
            return when(buy_house){
                0->{
                    null//"不限"
                }
                1->{
                    "买了"
                }
                2->{
                    "没买"
                }
                3->{
                    "和1家人同住/已购房2/租房3/打算婚后购房4/住在单位宿舍5"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_house)
        }
        fun getIndustry_str(industry_str:String?): Label?{
            return industry_str?.toLabel(R.mipmap.ic_label_work)
        }
    }

    /**
     * 工作城市
     */
    fun getCurrentResidence(): Label?{
//        return findIndustry(base?.work_city_num?.toIntOrNull()?:0)
        return (base?.work_province_str?.let { "现居${it}" })?.toLabel(R.mipmap.ic_label_residence)
    }

    fun getHeight(): Label?{
        return ((base?.height?:return null).toString()+"cm").toLabel(R.mipmap.ic_label_height)
    }
    fun getHometown(): Label?{
        return base?.hometown_city_str?.toLabel(R.mipmap.ic_label_hometown)
    }

    fun getHeightDemand(): Label?{
        val min_high=demand?.min_high?.toIntOrNull()?:0
        val max_high=demand?.max_high?.toIntOrNull()?:0
        if (max_high==0){
            return null
        }else {
            return "${min_high}-${max_high}cm".toLabel(R.mipmap.ic_label_height)
        }
    }
    fun getAgeDemand(): Label?{
        val age_min=demand?.age_min?:0
        val age_max=demand?.age_max?:0
        if (age_max==0){
            return null
        }else {
            return "${age_min}-${age_max}岁".toLabel(R.mipmap.ic_label_age)
        }
    }

    fun getBaseLabel():List<Label>{
        val list=ArrayList<Label>()
        getIndustry_str(base?.industry_str)?.also {
            list.add(it)
        }
        getHometown()?.also {
            list.add(it)
        }
        getCurrentResidence()?.also {
            list.add(it)
        }
        getHeight()?.also {
            list.add(it)
        }
        getEducationStr(base?.education)?.also {
            list.add(it)
        }
        getMarry_hadStr(base?.marry_had)?.also {
            list.add(it)
        }
        return list
//        return listOf("黑龙江牡丹江人","现居深圳","180cm","年收入30~60万","年收入30~60万","年收入30~60万","年收入30~60万")
    }

    fun getDemandWork_place_str(): Label?{
        return ("现居"+(demand?.work_place_str?:return null)).toLabel(R.mipmap.ic_label_residence)
    }

    fun getDemandLabel():List<Label>{
        val list=ArrayList<Label>()
        getAgeDemand()?.also {
            list.add(it)
        }
        getHeightDemand()?.also {
            list.add(it)
        }
        when (getUserSex()){
            Sex.male -> {
                getFigure_nv(demand?.figure_nv)?.also {
                    list.add(it)
                }
            }
            Sex.female -> {
                getFigure_nan(demand?.figure_nan?.toIntOrNull())?.also {
                    list.add(it)
                }
            }
            Sex.unknown -> {

            }
        }
        getSalary_range(demand?.salary_range)?.also {
            list.add(it)
        }

        getEducationStr(demand?.education)?.also {
            list.add(it)
        }
        getMarry_hadStr(demand?.marry_status)?.also {
            list.add(it)
        }
        getChild_hadStr(demand?.child_had)?.also {
            list.add(it)
        }
        getWant_childStr(demand?.want_child)?.also {
            list.add(it)
        }
        getIs_smoking(demand?.is_smoking)?.also {
            list.add(it)
        }
        getDrink_wine(demand?.drink_wine)?.also {
            list.add(it)
        }
        getIs_headface(demand?.is_headface)?.also {
            list.add(it)
        }
        getIndustry_str(demand?.industry_str)?.also {
            list.add(it)
        }
        getMarry_time(demand?.marry_time)?.also {
            list.add(it)
        }
        getBuy_car(demand?.buy_car)?.also {
            list.add(it)
        }
        getBuy_house(demand?.buy_house)?.also {
            list.add(it)
        }
        getDemandWork_place_str()?.also {
            list.add(it)
        }

        return list
    }
}