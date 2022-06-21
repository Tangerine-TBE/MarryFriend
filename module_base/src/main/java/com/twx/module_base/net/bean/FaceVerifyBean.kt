package com.twx.module_base.net.bean

/**
 * @author: Administrator
 * @date: 2022/5/20
 *
 * 百度人脸实名认证
 *
 */
data class FaceVerifyBean(
    val cached: Int,
    val error_code: Int,
    val error_msg: String,
    val log_id: Long,
    val result: FaceVerifyResult,
    val timestamp: Int
)

data class FaceVerifyResult(
    val score: Double
)