package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/16
 *
 * 行业
 *
 */
data class IndustryBean(
    val code: Int,
    val data: List<IndustryData>,
    val msg: String,
)

data class IndustryData(
    val id: Int,
    val name: String,
    val pid: Int,
)