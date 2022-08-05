package com.message.conversations


class ConversationsBean(val userId:String) {
    var conversationType= ConversationType.Chat
    var unReaderCount=0
    var lastMassage:String?=null
    var lastTime=0L

}