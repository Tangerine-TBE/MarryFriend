package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/24
 */
data class DemandAddressBean(
    val code: Int,
    val data: List<DemandAddressData>,
    val msg: String,
)

data class DemandAddressData(
    val id: Int,
    val user_id: Int,
    val work_city_code: Int,
    val work_city_str: String,
    val work_province_code: Int,
    val work_province_str: String,
)