package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/20
 *
 * 百度人脸识别(鉴黄)
 *
 */
data class FaceDetectBean(
    val conclusion: String,
    val conclusionType: Int,
    val `data`: List<FaceDetectData>,
    val isHitMd5: Boolean,
    val log_id: Long,
    val error_code: String,
    val error_msg: String,
)

data class FaceDetectData(
    val conclusion: String,
    val conclusionType: Int,
    val msg: String,
    val stars: List<FaceDetectStar>,
    val subType: Int,
    val type: Int,
)

data class FaceDetectStar(
    val name: String,
    val probability: String,
)