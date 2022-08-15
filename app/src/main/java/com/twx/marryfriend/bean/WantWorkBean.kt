package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/20
 *
 * 工作地区上传的数据类型
 *
 */
data class WantWorkBean(
    val user_id	: String,
    val work_province_code: Int,
    val work_province_str: String,
    val work_city_code: Int,
    val work_city_str: String,
)