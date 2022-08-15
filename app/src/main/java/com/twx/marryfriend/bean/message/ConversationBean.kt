package com.twx.marryfriend.bean.message

data class ConversationBean(
    var code: Int? = null,
    var `data`: List<FriendInfo>? = null,
    var msg: String? = null
)