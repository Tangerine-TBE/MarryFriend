package com.message.chat

import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage

class CustomMessage constructor(emMessage: EMMessage):Message<EMCustomMessageBody>(emMessage) {
    override fun getTypeDes()="自定义消息"
}