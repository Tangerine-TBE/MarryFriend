package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/20
 *
 * 百度文字审核识别
 *
 */
data class TextVerifyBean(
    val conclusion: String,
    val conclusionType: Int,
    val `data`: List<TextVerifyData>,
    val log_id: Long,
    val error_code: Int,
    val error_msg: String
)

data class TextVerifyData(
    val conclusion: String,
    val conclusionType: Int,
    val hits: List<TextVerifyHit>,
    val msg: String,
    val subType: Int,
    val type: Int
)

data class TextVerifyHit(
    val datasetName: String,
    val words: List<String>
)