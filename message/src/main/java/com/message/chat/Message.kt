package com.message.chat

import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMMessageBody

sealed class Message<BODY: EMMessageBody>(protected val emMessage: EMMessage){
    companion object{
        fun toMyMessage(emMessage: EMMessage):Message<out EMMessageBody>?{
            return when(emMessage.type){
                EMMessage.Type.TXT -> {
                    TxtMessage(emMessage)
                }
                EMMessage.Type.IMAGE -> {
                    ImageMessage(emMessage)
                }
                EMMessage.Type.VIDEO -> {
                    VideoMessage(emMessage)
                }
                EMMessage.Type.LOCATION -> {
                    LocationMessage(emMessage)
                }
                EMMessage.Type.VOICE -> {
                    VoiceMessage(emMessage)
                }
                EMMessage.Type.FILE -> {
                    FileMessage(emMessage)
                }
                EMMessage.Type.CMD -> {
                    CmdMessage(emMessage)
                }
                EMMessage.Type.CUSTOM -> {
                    CustomMessage(emMessage)
                }
                null->{
                    null
                }
            }
        }
    }
    val msgId:String=emMessage.msgId
    val from:String=emMessage.from
    val msgTime=emMessage.msgTime

    abstract fun getTypeDes():String

    fun getBody():BODY?{
        return emMessage.body as? BODY
    }
}