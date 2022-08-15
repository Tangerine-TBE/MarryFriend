package com.message.chat

import com.hyphenate.chat.EMCmdMessageBody
import com.hyphenate.chat.EMMessage

class CmdMessage constructor(emMessage: EMMessage):Message<EMCmdMessageBody>(emMessage) {
    override fun getTypeDes()="透传消息"
}