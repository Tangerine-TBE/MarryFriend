package com.message

import com.hyphenate.EMMessageListener
import com.hyphenate.chat.*
import com.hyphenate.exceptions.HyphenateException
import com.message.chat.ImageMessage
import com.message.chat.Message
import com.message.chat.TxtMessage
import com.message.conversations.ConversationType
import com.message.conversations.ConversationsBean
import com.xyzz.myutils.show.iLog


//https://docs-im.easemob.com/im/android/basics/message
//http://sdkdocs.easemob.com/apidoc/android/chat3.0/annotated.html
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
                this.conversationType= ConversationType.toMyType(conversation.type) ?:throw IllegalStateException("还未实现该会话")
            }
        }
        return conversationsBeanList
    }

    fun getHistoryMessage(toChatUsername:String, chatType: EMConversation.EMConversationType, pageSize:Int, msgId:String?=null):List<Message<out EMMessageBody>>{
        val resultList=ArrayList<EMMessage>()
        try {
            EMClient.getInstance().chatManager().fetchHistoryMessages(
                toChatUsername, chatType, pageSize, msgId
            )
            val conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername)
            val ramMsg = conversation.allMessages
            if (msgId!=null&&msgId.isNotBlank()){
                val index=ramMsg.indexOfFirst {
                    it.msgId==msgId
                }
                if (index==-1){
                    resultList.addAll(conversation.loadMoreMsgFromDB(msgId, pageSize))
                }else{
                    resultList.addAll(ramMsg.slice(index+1 until ramMsg.size))
                    resultList.addAll(conversation.loadMoreMsgFromDB(ramMsg[index].msgId,pageSize-index-1))
                }
            }else{
                resultList.addAll(ramMsg)
                if (ramMsg.size<pageSize){
                    resultList.addAll(conversation.loadMoreMsgFromDB(null,pageSize-ramMsg.size))
                }
            }
        } catch (e: HyphenateException) {
            e.printStackTrace()
        }

        return resultList.mapNotNull {
            Message.toMyMessage(it)
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
        iLog("用户${EMClient.getInstance().currentUser}注册消息监听")
        EMClient.getInstance().chatManager().removeMessageListener(msgListener)
        EMClient.getInstance().chatManager().addMessageListener(msgListener)
    }
}