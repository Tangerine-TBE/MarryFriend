package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/24
 */
data class ViewHeadfaceBean(
    val code: Int,
    val data: List<ViewHeadfaceData>,
    val msg: String
)

data class ViewHeadfaceData(
    val content: String,
    val create_time: String,
    val file_name: String,
    val file_type: String,
    val id: Int,
    val image_height: String,
    val image_url: String,
    val image_width: String,
    val real_status: Int,
    val status: Int,
    val user_id: Int
)