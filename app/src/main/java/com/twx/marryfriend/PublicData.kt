package com.twx.marryfriend

import com.blankj.utilcode.util.SPStaticUtils
import com.google.gson.Gson
import com.twx.marryfriend.base.BaseConstant
import com.twx.marryfriend.bean.CityBean
import com.twx.marryfriend.bean.post.PostClassBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private var cityBean:CityBean?=null
fun getCityData(): CityBean?{
    return cityBean?:
     try {
        val jsonString= SPStaticUtils.getString(Constant.CITY_JSON_DATE)
        val gson= Gson()
        val c=gson.fromJson(jsonString, CityBean::class.java)
         cityBean=c
         c
    }catch (e:Exception){
        null
    }
}

suspend fun getCityDataTryFromNet() = suspendCoroutine<CityBean> {continuation->
    getCityData()?.also {
        continuation.resume(it)
        return@suspendCoroutine
    }
    NetworkUtil.sendPost("${Contents.USER_URL}/marryfriend/GetParameter/shengShi", mapOf(),{
        try {
            SPStaticUtils.put(Constant.CITY_JSON_DATE,it)
            val gson= Gson()
            val c=gson.fromJson(it, CityBean::class.java)
            cityBean=c
            continuation.resume(c)
        }catch (e:Exception){
            continuation.resumeWithException(e)
        }
    },{
        continuation.resumeWithException(Exception(it))
    })
}

fun getOccupationData():PostClassBean?{
    return try {
        val text= BaseConstant.application.resources.assets.open("occupation/occupation.json").let {
            BufferedReader(InputStreamReader(it)).readText()
        }
        val gson=Gson()
        gson.fromJson(text, PostClassBean::class.java)
    }catch (e:Exception){
        null
    }
}