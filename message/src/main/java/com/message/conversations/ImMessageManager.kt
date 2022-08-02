package com.message.conversations

import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import com.xyzz.myutils.show.iLog
import kotlin.collections.ArrayList


//https://docs-im.easemob.com/im/android/basics/message
object ImMessageManager {
    private val msgListener by lazy {
        object : EMMessageListener {
            override fun onMessageReceived(messages: MutableList<EMMessage>?) {
                //收到消息
                iLog(messages?.firstOrNull()?.from,"收到消息,收到")
            }

            override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {
                //收到透传消息
                iLog(messages?.firstOrNull()?.from,"收到消息,cmd")
            }

            override fun onMessageRead(messages: MutableList<EMMessage>?) {
                //收到已读回执
                iLog(messages?.firstOrNull()?.from,"收到消息,已读")
            }

            override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
                //收到已送达回执
                iLog(messages?.firstOrNull()?.from,"收到消息,已送达")
            }

            override fun onMessageRecalled(messages: MutableList<EMMessage>?) {
                //消息状态变动
                iLog(messages?.firstOrNull()?.from,"收到消息,状态")
            }

        }
    }

    fun getAllConversations():List<ConversationsBean>{
        val conversations = EMClient.getInstance().chatManager().allConversations
        val conversationsBeanList=ArrayList<ConversationsBean>()
        conversations.keys.forEach {
            val conversation=conversations[it]
            ConversationsBean(it).apply {
                conversationsBeanList.add(this)
                this.unReaderCount=conversation?.unreadMsgCount?:0
                val lastMsg=conversation?.lastMessage?:return@apply
                this.lastTime= lastMsg.msgTime
                this.lastMassage=
                when(lastMsg.type){
                    EMMessage.Type.TXT -> {
                        (lastMsg.body as? EMTextMessageBody)?.message
                    }
                    EMMessage.Type.IMAGE -> {
                        "[图片]"
                    }
                    EMMessage.Type.VIDEO -> {
                        "[视频]"
                    }
                    EMMessage.Type.LOCATION -> {
                        "[位置]"
                    }
                    EMMessage.Type.VOICE -> {
                        "[语音]"
                    }
                    EMMessage.Type.FILE -> {
                        "[文件]"
                    }
                    EMMessage.Type.CMD -> {
                        "[透传]"
                    }
                    EMMessage.Type.CUSTOM -> {
                        "用户自定义消息"
                    }
                    null -> {
                        null
                    }
                }
                when(conversation.type){
                    EMConversation.EMConversationType.Chat -> {
                        this.conversationType=ConversationsBean.ConversationType.Chat
                    }
                    EMConversation.EMConversationType.GroupChat -> {

                    }
                    EMConversation.EMConversationType.ChatRoom -> {

                    }
                    EMConversation.EMConversationType.DiscussionGroup -> {

                    }
                    EMConversation.EMConversationType.HelpDesk -> {
                        this.conversationType=ConversationsBean.ConversationType.Assistant
                    }
                    null -> {

                    }
                }
            }
        }
        return conversationsBeanList
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
        iLog("用户${EMClient.getInstance().currentUser}注册消息监听")
        EMClient.getInstance().chatManager().removeMessageListener(msgListener)
        EMClient.getInstance().chatManager().addMessageListener(msgListener)
    }
}