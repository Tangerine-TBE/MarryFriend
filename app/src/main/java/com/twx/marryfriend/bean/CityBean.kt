package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/13
 */

data class CityBean(
    val code: Int,
    val data: List<Province>,
    val msg: String
)

// 省
data class Province(
    val child: List<City>,
    val id: Int,
    val name: String,
    val pid: Int  // 无用

)

// 市
data class City(
    val child: List<Any>,
    val id: Int,
    val name: String,
    val pid: Int  // 省编码
)
