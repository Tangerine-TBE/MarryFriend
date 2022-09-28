package com.hyphenate.easeim.common.model

data class ChatInfoBean(
    var userId: String = "",
    var targetId: String = "",
) {
    constructor() : this("", "")
}