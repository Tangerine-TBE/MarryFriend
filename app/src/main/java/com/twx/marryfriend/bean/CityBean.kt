package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/13
 */

data class CityBean(
    val code: Int,
    val `data`: List<Province>,
    val msg: String
)

// 省
data class Province(
    val cityList: List<City>,
    val code: String,
    val name: String
)

// 市
data class City(
    val areaList: List<Area>,
    val code: String,
    val name: String
)

// 县
data class Area(
    val code: String,
    val name: String
)