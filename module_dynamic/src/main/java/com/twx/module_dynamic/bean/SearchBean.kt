package com.twx.module_dynamic.bean

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
data class SearchBean(
    val message: String,
    val result_type: String,
    val results: List<SearchData>,
    val status: Int,
)

data class SearchData(
    val address: String,
    val area: String,
    val city: String,
    val detail: Int,
    val location: Location,
    val name: String,
    val province: String,
    val street_id: String,
    val uid: String,
)

data class Location(
    val lat: Double,
    val lng: Double,
)