package com.message.chat

import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMVoiceMessageBody

class VoiceMessage constructor(emMessage: EMMessage):Message<EMVoiceMessageBody>(emMessage) {
    override fun getTypeDes()="语音消息"
    private val body by lazy {
        getBody()
    }
    val length by lazy {
        body?.length
    }
    val lengthShow by lazy {
        "${length}''"
    }
    val lengthSpace by lazy {
        val str=StringBuilder()
        for (i in 0 until (length?:0)){
            str.append(" ")
        }
        str.toString()
    }
    val uri by lazy {
        body?.localUri
    }
    val fileSize by lazy {
        body?.fileSize
    }
    val fileName by lazy {
        body?.fileName
    }
}