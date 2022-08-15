package com.message.chat

import com.hyphenate.chat.EMLocationMessageBody
import com.hyphenate.chat.EMMessage

class LocationMessage constructor(emMessage: EMMessage):Message<EMLocationMessageBody>(emMessage) {
    override fun getTypeDes()="位置消息"
    private val body by lazy {
        getBody()
    }
}