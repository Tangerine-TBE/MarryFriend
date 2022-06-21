package com.twx.module_base.net.bean

/**
 * @author: Administrator
 * @date: 2022/5/20
 *
 * 获取 百度api所需的 Access Toke
 *
 */
data class IdentityVerifyBean(
    val cached: Int,
    val error_code: Int,
    val error_msg: String,
    val log_id: Any,
    val result: Any,
    val timestamp: Int,

    )