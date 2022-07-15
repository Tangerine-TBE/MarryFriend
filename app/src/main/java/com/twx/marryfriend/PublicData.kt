package com.twx.marryfriend

import com.blankj.utilcode.util.SPStaticUtils
import com.google.gson.Gson
import com.twx.marryfriend.base.BaseConstant
import com.twx.marryfriend.bean.CityBean
import com.twx.marryfriend.bean.post.PostClassBean
import com.twx.marryfriend.constant.Constant
import java.io.BufferedReader
import java.io.InputStreamReader

fun getCityData(): CityBean?{
    return try {
        val jsonString= SPStaticUtils.getString(Constant.CITY_JSON_DATE)
        val gson= Gson()
        gson.fromJson(jsonString, CityBean::class.java)
    }catch (e:Exception){
        null
    }
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