package com.message.chat

import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMMessageBody
import com.xyzz.myutils.show.iLog

sealed class Message<BODY: EMMessageBody>(val emMessage: EMMessage){
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
                    val event=(emMessage.body as EMCustomMessageBody).event()
                    val customEvent=CustomMessage.CustomEvent.values().find {
                        it.code==event
                    }
                    when(customEvent){
                        CustomMessage.CustomEvent.flower -> {
                            SendFlowerMessage(emMessage)
                        }
                        null -> {
                            iLog("未定义的类型,${event}")
                            null
                        }
                    }

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

    fun isSendSuccess():Boolean{
        return emMessage.isDelivered
    }

    abstract fun getTypeDes():String

    fun getBody():BODY?{
        return emMessage.body as? BODY
    }
}