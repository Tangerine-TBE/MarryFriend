package com.twx.marryfriend.bean.vip

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
data class PreviewOtherBean(
    val code: Int,
    val data: PreviewOtherData,
    val msg: String,
)

data class PreviewOtherData(
    val base_info: BaseInfo,
    val photos_count: Int,
    val photos_info: List<PhotosInfo>,
    val trends_count: Int,
    val trends_info: TrendsInfo,
)

data class BaseInfo(
    val age: Int,
    val birthday: String,
    val close_time_high: String,
    val close_time_low: String,
    val education: Int,
    val identity_status: Int,
    val image_url: String,
    val industry_str: String,
    val introduce_self: String,
    val level_high: Int,
    val level_low: Int,
    val nick: String,
    val occupation_str: String,
    val real_face: Int,
    val salary_range: Int,
    val user_sex: Int,
    val work_city_str: String,
    val work_province_str: String,
    val height: Int,
)

data class PhotosInfo(
    val content: String,
    val create_time: String,
    val file_name: String,
    val file_type: String,
    val id: Int,
    val image_height: String,
    val image_url: String,
    val image_width: String,
    val status: Int,
    val user_id: Int,
)

data class TrendsInfo(
    val audit_status: Int,
    val create_time: String,
    val id: Int,
    val image_url: String,
    val jingdu: String,
    val label: String,
    val position: String,
    val text_content: String,
    val trends_type: Int,
    val user_id: String,
    val video_cover: String,
    val video_url: String,
    val weidu: String,
)