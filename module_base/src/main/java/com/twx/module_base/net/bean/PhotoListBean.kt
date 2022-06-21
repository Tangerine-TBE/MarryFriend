package com.twx.module_base.net.bean

/**
 * @author: Administrator
 * @date: 2022/6/13
 */
data class PhotoListBean(
    val code: Int,
    val `data`: List<PhotoListData>,
    val msg: String
)

data class PhotoListData(
    val content: String,
    val create_time: String,
    val file_name: String,
    val file_type: String,
    val id: Int,
    val image_url: String,
    val kind: Int,
    val status: Int,
    val user_id: Int
)