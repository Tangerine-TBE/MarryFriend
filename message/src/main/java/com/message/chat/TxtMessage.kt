package com.message.chat

import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody

class TxtMessage constructor(emMessage: EMMessage):Message<EMTextMessageBody>(emMessage) {
    override fun getTypeDes()="文本消息"
    private val body by lazy {
        getBody()
    }
    val message by lazy {
        body?.message
    }
}