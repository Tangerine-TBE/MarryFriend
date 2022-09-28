package com.message.chat

import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage

sealed class CustomMessage constructor(emMessage: EMMessage):Message<EMCustomMessageBody>(emMessage) {
    enum class CustomEvent(val title:String,val code:String){
        flower("送花","flower"),
        security("安全提示","security"),
        openSuperVip("开通vip","openvip"),
        upload_head("上传头像","uploadhead");

        companion object{
            fun getTip(code: String):String?{
                return CustomEvent.values().find {
                    it.code==code
                }?.title?.let {
                    "[${it}]"
                }
            }
        }
    }

    override fun getTypeDes()="自定义消息"
}