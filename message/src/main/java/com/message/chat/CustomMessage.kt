package com.message.chat

import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage

sealed class CustomMessage constructor(emMessage: EMMessage):Message<EMCustomMessageBody>(emMessage) {
    enum class CustomEvent(title:String,val code:String){
        flower("送花","flower")
    }

    override fun getTypeDes()="自定义消息"
}