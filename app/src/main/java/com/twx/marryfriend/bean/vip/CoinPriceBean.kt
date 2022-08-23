package com.twx.marryfriend.bean.vip

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
data class CoinPriceBean(
    val code: Int,
    val `data`: List<CoinPriceData>,
    val msg: String
)

data class CoinPriceData(
    val describe: String,
    val level: Int,
    val mark: String,
    val name: String,
    val now_price: String,
    val original_price: String
)