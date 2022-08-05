package com.message.chat

import com.hyphenate.chat.EMImageMessageBody
import com.hyphenate.chat.EMMessage

class ImageMessage constructor(emMessage: EMMessage):Message<EMImageMessageBody>(emMessage) {
    override fun getTypeDes()="图片消息"
    private val body by lazy {
        getBody()
    }
    val thumbnailUrl by lazy {
        body?.thumbnailUrl
    }
    val width by lazy {
        body?.width
    }
    val height by lazy {
        body?.height
    }
    val isSendOriginalImage by lazy {
        body?.isSendOriginalImage
    }
    val fileName by lazy {
        body?.fileName
    }
    val fileSize by lazy {
        body?.fileSize
    }
    val thumbnailLocalPath by lazy {
        body?.thumbnailLocalPath()
    }
    val thumbnailLocalUri by lazy {
        body?.thumbnailLocalUri()
    }
}