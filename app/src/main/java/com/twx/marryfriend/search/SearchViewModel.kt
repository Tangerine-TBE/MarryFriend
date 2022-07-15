package com.twx.marryfriend.search

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.UserInfo.getUserSex
import com.twx.marryfriend.UserInfo.reversalSex
import com.twx.marryfriend.bean.City
import com.twx.marryfriend.bean.Province
import com.twx.marryfriend.bean.post.OccupationBean
import com.twx.marryfriend.bean.post.OccupationDataBean
import com.twx.marryfriend.bean.search.SearchResultBean
import com.twx.marryfriend.bean.search.SearchResultItem
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.enumeration.*
import com.xyzz.myutils.NetworkUtil
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SearchViewModel:ViewModel() {
    companion object{
        const val MAX_AGE=100
        const val MIN_AGE=18
        const val MAX_HEIGHT=240
        const val MIN_HEIGHT=120
    }
    private val searchParameterMap by lazy {
        HashMap<String,String>().apply {
            this["page"]="1"
            this["size"]="20"
            this["guest_sex"]= reversalSex(getUserSex()).toString()
        }
    }

    fun setParameter(map: Map<String,String>){
        searchParameterMap.clear()
        searchParameterMap.putAll(map)
    }

    fun getParameter():Map<String,String>{
        return searchParameterMap
    }

    suspend fun filtrateSearch()= suspendCoroutine<List<SearchResultItem>>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/filtrateSearch"
        val map= mapOf(
            "user_id" to UserInfo.getUserId(),
            "user_level" to UserInfo.getUserVipLevel().toString()
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val gson=Gson()
                coroutine.resume(gson.fromJson(response,SearchResultBean::class.java).data?.list?: emptyList())
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        },searchParameterMap)
    }

    suspend fun accurateSearch(text:String) = suspendCoroutine<List<SearchResultItem>> {coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/idNickSearch"
        val map= mapOf(
            "user_id" to UserInfo.getUserId(),
            "id_nick" to text
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val gson=Gson()
                coroutine.resume(gson.fromJson(response,SearchResultBean::class.java).data?.list?: emptyList())
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        }, mapOf("page" to "1","size" to "20"))
    }

    fun setAgeParameter(intRange:IntRange?){
        val key1="age_min"
        val key2="age_max"
        if (intRange==null){
            searchParameterMap.remove(key1)
            searchParameterMap.remove(key2)
        }else{
            searchParameterMap[key1]= intRange.first.toString()
            searchParameterMap[key2]= intRange.last.toString()
        }
    }

    fun setHeightParameter(intRange:IntRange?){
        val key1="height_min"
        val key2="height_max"
        if (intRange==null){
            searchParameterMap.remove(key1)
            searchParameterMap.remove(key2)
        }else{
            searchParameterMap[key1]= intRange.first.toString()
            searchParameterMap[key2]= intRange.last.toString()
        }
    }

    fun setEduParameter(listParameter:List<EduEnum>?){
        val key1="education"
        if (listParameter.isNullOrEmpty()){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.map { it.code }.toJsonString()
        }
    }

    fun setWorkCityParameter(listParameter:List<Pair<Province, City>>?){
        val key1="work_city_num"
        if (listParameter.isNullOrEmpty()){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.map { it.second.code }.toJsonString()
        }
    }

    fun setSalaryParameter(intRange:IntRange?){//TODO http://mindoc.aisou.club/docs/faskjfasfioasuviovifadascd/faskjfasfioasuviovifadascd-1du3hkcr10lag
        return
        val key1="salary_range"
        val key2="age_max"
        if (intRange==null){
            searchParameterMap.remove(key1)
            searchParameterMap.remove(key2)
        }else{
            searchParameterMap[key1]= intRange.first.toString()
            searchParameterMap[key2]= intRange.last.toString()
        }
    }

    fun setMarriageParameter(listParameter:List<MarriageEnum>?){
        val key1="marry_had"
        if (listParameter.isNullOrEmpty()){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.map { it.code }.toJsonString()
        }
    }

    fun setOccupationParameter(listParameter: Pair<OccupationDataBean,OccupationBean>?){
        val key1="occupation_num"
        if (listParameter==null){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.second.id.toString()
        }
    }

    fun setNativePlaceParameter(listParameter:List<Pair<Province, City>>?){
        val key1="hometown_city_num"
        if (listParameter.isNullOrEmpty()){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.map { it.second.code }.toJsonString()
        }
    }

    fun setHousingParameter(listParameter:List<HousingEnum>?){
        val key1="buy_house"
        if (listParameter.isNullOrEmpty()){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.map { it.code }.toJsonString()
        }
    }

    fun setBuyCarParameter(listParameter: BuyCarEnum?){
        val key1="buy_car"
        if (listParameter==null){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.code.toString()
        }
    }

    fun setWantChildrenParameter(listParameter:List<WantChildrenEnum>?){
        val key1="child_had"
        if (listParameter.isNullOrEmpty()){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.map { it.code }.toJsonString()
        }
    }

    fun setConstellationParameter(listParameter:List<ConstellationEnum>?){
        val key1="constellation"
        if (listParameter.isNullOrEmpty()){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.map { it.code }.toJsonString()
        }
    }

    fun setHavePortraitParameter(listParameter: HeadPortraitEnum?){
        val key1="headface"
        if (listParameter==null){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.code.toString()
        }
    }

    fun setVipParameter(listParameter: IsVipEnum?){
        val key1="level"
        if (listParameter==null){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.code.toString()
        }
    }

    fun setRealNameParameter(listParameter: RealNameEnum?){
        val key1="verify"
        if (listParameter==null){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.code.toString()
        }
    }

    fun setOnLineParameter(listParameter: OnLineEnum?){
        val key1="active"
        if (listParameter==null){
            searchParameterMap.remove(key1)
        }else{
            searchParameterMap[key1] = listParameter.code.toString()
        }
    }


    private fun <T> List<T>.toJsonString():String{
        val gson=Gson()
        return gson.toJson(this)
    }
}