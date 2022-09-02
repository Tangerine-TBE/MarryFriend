package com.message.chat

import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage

sealed class CustomMessage constructor(emMessage: EMMessage):Message<EMCustomMessageBody>(emMessage) {
    enum class CustomEvent(title:String,val code:String){
        flower("送花","flower"),
        security("安全提示","security"),
        openVip("开通vip","openVip"),
        upload_head("上传头像","uploadHead")
    }

    override fun getTypeDes()="自定义消息"
}