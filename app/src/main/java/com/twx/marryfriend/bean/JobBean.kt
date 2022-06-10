package com.twx.marryfriend.bean

/**
 * @author: Administrator
 * @date: 2022/5/16
 *
 * 岗位
 *
 */
data class JobBean(
    val code: Int,
    val `data`: List<JobData>,
    val msg: String,
)

data class JobData(
    val child: List<JobChild>,
    val id: Int,
    val name: String,
    val pid: Int,
)

data class JobChild(
    val child: List<Any>,
    val id: Int,
    val name: String,
    val pid: Int,
)