package com.twx.module_base.net.bean

/**
 * @author: Administrator
 * @date: 2022/5/20
 *
 * 获取 百度api所需的 Access Toke
 *
 */
data class AccessTokenBean(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val scope: String,
    val session_key: String,
    val session_secret: String
)