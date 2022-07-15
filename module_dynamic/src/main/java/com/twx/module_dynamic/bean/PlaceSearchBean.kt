package com.twx.module_dynamic.bean

/**
 * @author: Administrator
 * @date: 2022/6/27
 */
data class PlaceSearchBean(
    val count: String,
    val info: String,
    val infocode: String,
    val pois: List<Poi>,
    val status: String,
    val suggestion: Suggestion,
)

data class Poi(
    val address: String,
    val adname: String,
    val biz_ext: Any,
    val biz_type: Any,
    val childtype: Any,
    val cityname: String,
    val distance: String,
    val id: String,
    val importance: Any,
    val location: String,
    val name: String,
    val parent: Any,
    val photos: Any,
    val pname: String,
    val poiweight: Any,
    val shopid: Any,
    val shopinfo: String,
    val tel: Any,
    val type: String,
    val typecode: String,
)

data class Suggestion(
    val cities: List<Any>,
    val keywords: List<Any>,
)
