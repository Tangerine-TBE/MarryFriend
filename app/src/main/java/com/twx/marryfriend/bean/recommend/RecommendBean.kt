package com.twx.marryfriend.bean.recommend

import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.*
import com.twx.marryfriend.enumeration.ConstellationEnum
import com.twx.marryfriend.getAgeFromBirthday
import org.json.JSONArray
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
    val zhaohu: Zhaohu?=null,
    val headface:List<HeadfaceBean>?=null,
    val zo_place:List<Zo_place>?=null
){
    fun getId():Int{
        return base?.user_id?:throw IllegalStateException("id为空")
    }

    fun isFollow():Boolean{//是否关注
        return base?.focus_uid!=null
    }

    fun clearFollow(){
        base?.focus_uid=null
    }

    fun addFollow(){
        base?.focus_uid=getId()
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

        getEducationArrStr(demand?.getEducationArray())?.also {
            list.add(it)
        }
        getMarry_hadArrStr(demand?.getMarry_statusArray())?.also {
            list.add(it)
        }
        getChild_hadArrStr(demand?.getChild_hadArray())?.also {
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

    fun getBaseLabel(): List<Label> {
        val list = ArrayList<Label>()
        getAge(base?.birthday)?.also {
            list.add(it)
        }
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
        getSalary_range(base?.salary_range)?.also {
            list.add(it)
        }
        getEducationStr(base?.education)?.also {
            list.add(it)
        }
        getMarry_hadStr(base?.marry_had)?.also {
            list.add(it)
        }
        more?.weight?.let {
            if (it==0){
                null
            }else{
                it.toString()+"kg"
            }
        }?.toLabel(R.mipmap.ic_label_weight)?.also {
            list.add(it)
        }
        if (UserInfo.isSexMail(base?.user_sex)){
            getFigure_nan(more?.figure_nan?.toIntOrNull())
        }else{
            getFigure_nv(more?.figure_nv)
        }?.also {
            list.add(it)
        }
        ConstellationEnum.findConstellation(more?.constellation?.toIntOrNull())?.title?.toLabel(R.mipmap.ic_label_constellation)?.also {
            list.add(it)
        }
        getLove_target(more?.target_show,more?.love_target)?.also {
            list.add(it)
        }
        getNationality(more?.nationality?.toIntOrNull())?.also {
            list.add(it)
        }
        getBuy_car(more?.buy_car)?.also {
            list.add(it)
        }
        getBuy_house(more?.buy_house)?.also {
            list.add(it)
        }
        getIs_smoking(more?.is_smoking)?.also {
            list.add(it)
        }
        getIs_drinking(more?.is_drinking)?.also {
            list.add(it)
        }
        getChild_hadStr(more?.child_had)?.also {
            list.add(it)
        }
        getWant_childStr(more?.want_child)?.also {
            list.add(it)
        }
        getMarry_time(more?.marry_time)?.also {
            list.add(it)
        }
        return list
//        return listOf("黑龙江牡丹江人","现居深圳","180cm","年收入30~60万","年收入30~60万","年收入30~60万","年收入30~60万")
    }

    fun isLike():Boolean{
        return base?.like_uid!=null
    }
    fun isSuperLike():Boolean{
        return base?.super_uid!=null
    }

    fun isTaLikeMe():Boolean{
        return base?.like_uid!=null
    }
    /**
     * 头像
     */
    fun getHeadImg():String?{
        return headface?.firstOrNull()?.image_url
    }
    fun isHeadIdentification():Boolean{
        return headface?.firstOrNull()?.real_status==1
    }
    
    fun getLongitude():Double?{
//        trends?.get(0)?.jingdu
        return place?.jingdu?.toDoubleOrNull()
    }
    fun getLatitude():Double?{
        return place?.weidu?.toDoubleOrNull()
    }
    fun getNickname():String{
        return base?.nick?:""
    }
    fun getAge():Int?{
        return base?.birthday?.getAgeFromBirthday()?:base?.age
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
    fun getAboutMe():String?{
        return base?.introduce_self
    }
    fun getAboutMeHobby():String?{
        return base?.daily_hobbies
    }
    fun getExpectedTa():String?{
        return base?.ta_in_my_mind
    }
    fun getLifePhoto():List<Photo>{
        return photos?.filter { /*it.kind == 3*/true }?: emptyList()
    }

    fun getUserSex(): Sex {
        return Sex.toSex(base?.user_sex)
    }

    fun getVoiceUrl():String?{
        return zhaohu?.voice_url
    }

    fun getVoiceDurationStr():String{
        val duration=((zhaohu?.voice_long?.toIntOrNull()?:0)+500)/1000
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
        fun getEducationArrStr(education:List<Int>?): Label?{
            return education?.map {
                getEducationStr(it)?.label
            }.let {
                val str=StringBuilder()
                it?.forEach {
                    if (it!=null) {
                        str.append(it+"、")
                    }
                }
                if (str.isEmpty()){
                    null
                }else{
                    str.removeSuffix("、").toString()
                }
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
        fun getMarry_hadArrStr(education:List<Int>?): Label?{
            return education?.map {
                getMarry_hadStr(it)?.label
            }.let {
                val str=StringBuilder()
                it?.forEach {
                    if (it!=null) {
                        str.append(it+"、")
                    }
                }
                if (str.isEmpty()){
                    null
                }else{
                    str.removeSuffix("、").toString()
                }
            }?.toLabel(R.mipmap.ic_label_school)
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
        fun getChild_hadArrStr(education:List<Int>?): Label?{
            return education?.map {
                getChild_hadStr(it)?.label
            }.let {
                val str=StringBuilder()
                it?.forEach {
                    if (it!=null) {
                        str.append(it+"、")
                    }
                }
                if (str.isEmpty()){
                    null
                }else{
                    str.removeSuffix("、").toString()
                }
            }?.toLabel(R.mipmap.ic_label_school)
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
            }?.toLabel(R.mipmap.ic_label_head)
        }
        fun getSalary_range(salary_range:Int?): Label?{
            return when(salary_range){
                0->{
                    null//"不限"
                }
                1 -> {
                    "5k及以下"
                }
                2 -> {
                    "5k~8k"
                }
                3 -> {
                    "8k~12k"
                }
                4 -> {
                    "12k~16k"
                }
                5 -> {
                    "16k~20k"
                }
                6 -> {
                    "20k~35k"
                }
                7 -> {
                    "35k~50k"
                }
                8 -> {
                    "50k~70k"
                }
                9 -> {
                    "70k及以上"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_income)
        }
        private val salary by lazy {
            listOf(1 to 5000,
                2 to 8000,
                3 to 12000,
                4 to 16000,
                5 to 20000,
                6 to 35000,
                7 to 50000,
                8 to 70000)
        }
        fun getSalary_range(salary_range:String?): Label?{
            if (salary_range.isNullOrBlank()){
                return null
            }
            val jsonArray=JSONArray(salary_range)
            val length=jsonArray.length()
            if (length<=0){
                return null
            }else{
                val first=jsonArray.getInt(0)
                val last=jsonArray.getInt(length-1)
                return if (last==0){
                    null
                }else if (first==0){
                    salary.find {
                        it.first==last
                    }?.let {
                        "${it.second/1000}k 以下"
                    }?.toLabel(R.mipmap.ic_label_income)
                }else if (first== salary.last().first){
                    "${salary.last().second/1000}k 以上".toLabel(R.mipmap.ic_label_income)
                }else{
                    val d=salary.find {
                        it.first==first
                    }?: salary.first()
                    val u=salary.find {
                        it.first==last
                    }?: salary.last()
                    "${d.second/1000}k-${u.second/1000}k".toLabel(R.mipmap.ic_label_income)
                }
            }
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
                    "闪婚"
                }
                2->{
                    "一年内结婚"
                }
                3->{
                    "两年内结婚"
                }
                4->{
                    "三年内结婚"
                }
                5->{
                    "时机成熟就结婚"
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
                    "和家人同住"
                }
                2->{
                    "已购房"
                }
                3->{
                    "租房"
                }
                4->{
                    "打算婚后购房"
                }
                5->{
                    "住在单位宿舍"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_house)
        }
        fun getIndustry_str(industry_str:String?): Label?{
            return industry_str?.toLabel(R.mipmap.ic_label_work)
        }
        fun getIs_drinking(is_drinking:Int?): Label?{
            return when(is_drinking){
                0->{
                    null//"不限"
                }
                1->{
                    "经常喝酒"
                }
                2->{
                    "偶尔喝酒"
                }
                3->{
                    "完全不喝酒"
                }
                4->{
                    "社交场合会喝"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_drink)
        }

        fun getNationality(nationality:Int?): Label?{
            return listOf<Pair<Int,String?>>(
                0 to null,
                1 to "汉族",
                2 to "壮族",
                3 to "回族",
                4 to "满族",
                5 to "维吾尔族",
                6 to "苗族",
                7 to "彝族",
                8 to "土家族",
                9 to "藏族",
                10 to "蒙古族",
                11 to "侗族",
                12 to "布依族",
                13 to "瑶族",
                14 to "白族",
                15 to "朝鲜族",
                16 to "哈尼族",
                17 to "黎族",
                18 to "哈萨克族",
                19 to "傣族",
                20 to "畲族",
                21 to "傈僳族",
                22 to "东乡族",
                23 to "仡佬族",
                24 to "拉祜族",
                25 to "佤族",
                26 to "水族",
                27 to "纳西族",
                28 to "羌族",
                29 to "土族",
                30 to "仫佬族",
                31 to "锡伯族",
                32 to "柯尔克孜族",
                33 to "景颇族",
                34 to "达斡尔族",
                35 to "撒拉族",
                36 to "布朗族",
                37 to "毛南族",
                38 to "塔吉克族",
                39 to "普米族",
                40 to "阿昌族",
                41 to "怒族",
                42 to "鄂温克族",
                43 to "京族",
                44 to "基诺族",
                45 to "德昂族",
                46 to "保安族",
                47 to "俄罗斯族",
                48 to "裕固族",
                49 to "乌孜别克族",
                50 to "门巴族",
                51 to "鄂伦春族",
                52 to "独龙族",
                53 to "赫哲族",
                54 to "高山族",
                55 to "珞巴族",
                56 to "塔塔尔族"
                ).find {
                    nationality==it.first
            }?.second?.toLabel(R.mipmap.ic_label_nation)
        }

        fun getLove_target(target_show:Int?,love_target:Int?): Label?{
            if (target_show==2){
                return null
            }
            return when(love_target){
                0->{
                    null//"不限"
                }
                1->{
                    "短期内想结婚"
                }
                2->{
                    "先认真恋爱,合适就考虑结婚"
                }
                3->{
                    "先认真恋爱,后期再说"
                }
                4->{
                    "没考虑清楚,视情况而定"
                }
                else->{
                    null
                }
            }?.toLabel(R.mipmap.ic_label_love)
        }

        fun getAge(birthday: String?): Label? {
            return birthday?.getAgeFromBirthday()?.let {
                "${it}岁"
            }?.toLabel(R.mipmap.ic_label_age)
        }
    }

    /**
     * 工作城市
     */
    fun getCurrentResidence(): Label?{
//        return findIndustry(base?.work_city_num?.toIntOrNull()?:0)
        return "${base?.work_province_str}${base?.work_city_str}".toLabel(R.mipmap.ic_label_residence)?.also { it.label="${it.label}工作" }
    }

    fun getHeight(): Label?{
        return ((base?.height?:return null).toString()+"cm").toLabel(R.mipmap.ic_label_height)
    }
    fun getHometown(): Label?{
        return "${base?.hometown_province_str?:""}${base?.hometown_city_str?:""}".toLabel(R.mipmap.ic_label_hometown)?.also { it.label="${it.label}人" }
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

    fun getDemandWork_place_str(): Label?{
        val stringBuilder=StringBuilder()
        zo_place?.forEach {
            if (!it.work_city_str.isNullOrBlank()) {
                stringBuilder.append(it.work_city_str)
                stringBuilder.append("、")
            }
        }?:return null
        return if (stringBuilder.isBlank()){
            null
        }else{
            stringBuilder.removeSuffix("、").toString()+"工作"
        }?.toLabel(R.mipmap.ic_label_residence)
    }
}