package com.twx.module_dynamic.bean

/**
 * @author: Administrator
 * @date: 2022/6/28
 */
data class CommentBean(
    var list: CommentOneList,
    var twoList: MutableList<CommentTwoList>,
    var all: Int, // 子评论总数量
    var total: Int, // 剩余未展示的子评论数量
)