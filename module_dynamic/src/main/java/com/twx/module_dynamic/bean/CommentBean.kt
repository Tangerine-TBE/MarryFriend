package com.twx.module_dynamic.bean

/**
 * @author: Administrator
 * @date: 2022/6/28
 */
data class CommentBean(
    var list: CommentOneList,
    var twoList: MutableList<CommentTwoList>,
    var all: Int,
    var total: Int,
)