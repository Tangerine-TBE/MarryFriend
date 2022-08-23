package com.twx.marryfriend.bean.vip

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
data class VipPriceBean(
    val code: Int,
    val `data`: List<VipPriceData>,
    val msg: String,
)

data class VipPriceData(
    val describe: String,
    val level: Int,
    val mark: String,
    val name: String,
    val now_price: String,
    val original_price: String,
)