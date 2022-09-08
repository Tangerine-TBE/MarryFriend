package com.twx.marryfriend.bean.dynamic

/**
 * @author: Administrator
 * @date: 2022/6/27
 *
 *  记录点赞数据与关注数据
 *
 */
data class LikeBean(
    var trendID: Int,
    var focus: Boolean,
    var like: Boolean,
    var likeCount: Int,
    var anim: Boolean = false,
)