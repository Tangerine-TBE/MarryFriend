package com.message.conversations


class ConversationsBean(val userId:String) {
    enum class ConversationType(title:String){
        Chat("单聊"),
        Assistant("恋爱小管家")
    }
    var conversationType=ConversationType.Chat
    var unReaderCount=0
    var lastMassage:String?=null
    var lastTime=0L

}