package com.twx.module_base.net.bean

/**
 * @author: Administrator
 * @date: 2022/5/11
 */
data class VerifyCodeBean(
    val code: String,
    val data: Any,
    val msg: String,
)