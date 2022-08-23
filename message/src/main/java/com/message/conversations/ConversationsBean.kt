package com.message.conversations


class ConversationsBean constructor(val conversationId:String) {
    var conversationType= ConversationType.Chat
    var unReaderCount=0
    var lastMassage:String?=null
    var lastTime=0L
}