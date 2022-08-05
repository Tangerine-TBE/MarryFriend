package com.twx.marryfriend.message.model

import com.hyphenate.chat.EMMessageBody
import com.message.chat.Message
import com.xyzz.myutils.display.DateDisplayManager

class ChatItemModel<out DATA:Message<out EMMessageBody>>(val data:DATA) {
    companion object {
        private val dateFormat by lazy {
            DateDisplayManager.getMessageDataImpl()
        }
    }
    var imageHead:String?=null
    var nickname:String?=null
    val msgTime=data.msgTime

    val visibility=true
    val showTimeText by lazy {
        dateFormat.setTime(msgTime)
        dateFormat.toText()
    }
}