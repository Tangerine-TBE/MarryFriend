package com.message

import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.xyzz.myutils.show.iLog

object ImMessageManager {
    private val msgListener by lazy {
        object : EMMessageListener {
            override fun onMessageReceived(messages: MutableList<EMMessage>?) {
                //收到消息
                iLog(messages?.firstOrNull()?.from,"收到消息")
            }

            override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {
                //收到透传消息
            }

            override fun onMessageRead(messages: MutableList<EMMessage>?) {
                //收到已读回执
            }

            override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
                //收到已送达回执
            }

            override fun onMessageRecalled(messages: MutableList<EMMessage>?) {
                //消息状态变动
            }

        }
    }

    fun sendTextMsg(username: String,content:String){
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        val message = EMMessage.createTxtSendMessage(content, username)
        //如果是群聊，设置chattype，默认是单聊
//        if (chatType === CHATTYPE_GROUP) message.chatType = ChatType.GroupChat
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message)
    }

    fun startMessageListener(){
        EMClient.getInstance().chatManager().removeMessageListener(msgListener)
        EMClient.getInstance().chatManager().addMessageListener(msgListener)
    }
}