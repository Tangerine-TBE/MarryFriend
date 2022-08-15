package com.message.chat

import com.hyphenate.chat.EMFileMessageBody
import com.hyphenate.chat.EMMessage

class FileMessage constructor(emMessage: EMMessage):Message<EMFileMessageBody>(emMessage) {
    override fun getTypeDes()="文件消息"
    private val body by lazy {
        getBody()
    }
    val fileName by lazy {
        body?.fileName
    }
    val localUrl by lazy {
        body?.localUrl
    }
    val localUri by lazy {
        body?.localUri
    }
}