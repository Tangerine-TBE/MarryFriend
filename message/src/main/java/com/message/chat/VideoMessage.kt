package com.message.chat

import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMVideoMessageBody

class VideoMessage constructor(emMessage: EMMessage):Message<EMVideoMessageBody>(emMessage) {
    override fun getTypeDes()="视频消息"
    private val body by lazy {
        getBody()
    }
    val videoFileLength by lazy {
        body?.videoFileLength
    }
    val thumbnailUrl by lazy {
        body?.thumbnailUrl
    }
    val thumbnailWidth by lazy {
        body?.thumbnailWidth
    }
    val thumbnailHeight by lazy {
        body?.thumbnailHeight
    }
    val localThumb by lazy {
        body?.localThumb
    }
    val localThumbUri by lazy {
        body?.localThumbUri
    }
    val duration by lazy {
        body?.duration
    }

}