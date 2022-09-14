package com.message

import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.hyphenate.EMConversationListener
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.*
import com.hyphenate.exceptions.HyphenateException
import com.message.chat.CustomMessage
import com.message.chat.Message
import com.message.conversations.ConversationType
import com.message.conversations.ConversationsBean
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog


//https://docs-im.easemob.com/im/android/basics/message
//http://sdkdocs.easemob.com/apidoc/android/chat3.0/annotated.html
object ImMessageManager {

    val newMessageLiveData=MutableLiveData<List<Message<out EMMessageBody>>>()//收到新消息
    private val fromConversationRead=MutableLiveData<String>()
    private val messageRead=MutableLiveData<List<EMMessage>>()

    private val messageDelivered=MutableLiveData< MutableList<EMMessage>?>()

    fun observeMessageRead(owner: LifecycleOwner,observer: Observer<List<EMMessage>>){
        messageRead.value=null
        messageRead.observe(owner,observer)
    }

    fun observeConversationMessage(owner: LifecycleOwner,observer: Observer<String>){
        fromConversationRead.value=null
        fromConversationRead.observe(owner,observer)
    }

    fun observeNewMessage(owner: LifecycleOwner,observer: Observer<List<Message<out EMMessageBody>>>){
        newMessageLiveData.value=null
        newMessageLiveData.observe(owner,observer)
    }

    /**
     * 已送达
     */
    private fun observeMessageDelivered(owner: LifecycleOwner,observer: Observer<MutableList<EMMessage>?>){
        messageDelivered.value=null
        messageDelivered.observe(owner,observer)
    }

    private val msgListener by lazy {
        object : EMMessageListener {
            override fun onMessageReceived(messages: MutableList<EMMessage>?) {
                //收到消息
                iLog(messages?.firstOrNull()?.from,"收到消息,收到")
                newMessageLiveData.postValue(messages?.mapNotNull { Message.toMyMessage(it) })
            }

            override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {
                //收到透传消息
                iLog(messages?.firstOrNull()?.from,"收到消息,cmd")
            }

            override fun onMessageRead(messages: MutableList<EMMessage>?) {
                //收到已读回执
                iLog("收到消息,已读"+messages?.firstOrNull()?.from+"发给"+messages?.firstOrNull()?.to)
                messageRead.value=messages
            }

            override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
                //收到已送达回执
                iLog(messages?.firstOrNull()?.from,"收到消息,已送达")
                messageDelivered.value=messages
            }

            override fun onMessageRecalled(messages: MutableList<EMMessage>?) {
                //消息状态变动
                iLog(messages?.firstOrNull()?.from,"收到消息,状态")
            }

        }
    }

    private val conversationsListener by lazy {
        object : EMConversationListener{
            override fun onCoversationUpdate() {
                
            }

            override fun onConversationRead(from: String?, to: String?) {
                iLog("会话已读${from+"发送给"+to}")
                fromConversationRead.value=from
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

    fun getHistoryMessage(toChatUsername: String, pageSize: Int, msgId: String? = null):List<Message<out EMMessageBody>>{
        val resultList=ArrayList<EMMessage>()
        try {
            val conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername)?:return emptyList()
            val ramMsg = conversation.allMessages.asReversed()
            if (msgId!=null&&msgId.isNotBlank()){
                val index=ramMsg.indexOfFirst {
                    it.msgId==msgId
                }
                if (index==-1){
                    resultList.addAll(conversation.loadMoreMsgFromDB(msgId, pageSize))
                }else{
                    resultList.addAll(ramMsg.slice(index+1 until ramMsg.size))
                    resultList.addAll(conversation.loadMoreMsgFromDB(ramMsg[index].msgId,pageSize-resultList.size))
                }
            }else{
                resultList.addAll(ramMsg)
                if (ramMsg.size<pageSize){
                    resultList.addAll(conversation.loadMoreMsgFromDB(ramMsg.lastOrNull()?.msgId,pageSize-ramMsg.size))
                }
            }
        } catch (e: HyphenateException) {
            eLog(e.stackTraceToString())
        }
        return resultList.mapNotNull {
            Message.toMyMessage(it)
        }.sortedBy {
            -it.msgTime
        }
    }

    fun markAllMsgAsRead(username: String){
        val conversation = EMClient.getInstance().chatManager().getConversation(username)
        //指定会话消息未读数清零
        conversation.markAllMessagesAsRead()
//        //把一条消息置为已读
//        conversation.markMessageAsRead(messageId)
//        //所有未读消息数清零
//        EMClient.getInstance().chatManager().markAllConversationsAsRead()
    }

    fun ackConversationRead(conversationId:String){
        try {
            EMClient.getInstance().chatManager().ackConversationRead(conversationId)
        } catch (e: HyphenateException) {
            e.printStackTrace()
        }
    }

    fun sendReadAck(message: EMMessage) {
        //是接收的消息，未发送过read ack消息且是单聊
        if (message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked && message.chatType == EMMessage.ChatType.Chat) {
//            val type = message.type
            //视频，语音及文件需要点击后再发送,这个可以根据需求进行调整
//            if (type == EMMessage.Type.VIDEO || type == EMMessage.Type.VOICE || type == EMMessage.Type.FILE) {
//                return
//            }
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.from, message.msgId)
            } catch (e: HyphenateException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateMessage(msg:EMMessage){
        EMClient.getInstance().chatManager().updateMessage(msg)
    }

    fun sendTextMsg(username: String,content:String):Message<out EMMessageBody>?{
        iLog("发送\"${content}\"给${username}")
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        val message = EMMessage.createTxtSendMessage(content, username)
//        message.isAcked
//        message.isDelivered
//        message.progress()
        //如果是群聊，设置chattype，默认是单聊
//        if (chatType === CHATTYPE_GROUP) message.chatType = ChatType.GroupChat
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message)
        return Message.toMyMessage(message)
    }

    fun sendImageMsg(username: String,uri: Uri):Message<out EMMessageBody>?{
        val message=EMMessage.createImageSendMessage(uri,true,username)
        EMClient.getInstance().chatManager().sendMessage(message)
        return Message.toMyMessage(message)
    }

    fun sendImageMsg(username: String,file: String):Message<out EMMessageBody>?{
        val message=EMMessage.createImageSendMessage(file,true,username)
        EMClient.getInstance().chatManager().sendMessage(message)
        return Message.toMyMessage(message)
    }

    fun sendVoiceMsg(username: String,voiceUri:Uri,length:Int):Message<out EMMessageBody>?{
        //voiceUri 为语音文件本地资源标志符，length 为录音时间(秒)
        val message = EMMessage.createVoiceSendMessage(voiceUri, length, username)
//如果是群聊，设置 chattype，默认是单聊
//        message.chatType = ChatType.GroupChat
        EMClient.getInstance().chatManager().sendMessage(message)
        return Message.toMyMessage(message)
    }

    fun sendFlower(username: String):Message<out EMMessageBody>?{
        val customMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM)
// event为需要传递的自定义消息事件，比如礼物消息，可以设置event = "gift"
        val customBody = EMCustomMessageBody(CustomMessage.CustomEvent.flower.code)
// params类型为Map<String, String>
//        customBody.params = params
        customMessage.addBody(customBody)
// to指另一方环信id（或者群组id，聊天室id）
        customMessage.setTo(username)
// 如果是群聊，设置chattype，默认是单聊
        customMessage.chatType = EMMessage.ChatType.Chat
        EMClient.getInstance().chatManager().sendMessage(customMessage)
        return Message.toMyMessage(customMessage)
    }

    fun getCustomMessage(username: String, type:CustomMessage.CustomEvent):EMMessage?{
        val customMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM)
        val customBody = EMCustomMessageBody(type.code)
        customMessage.addBody(customBody)
        customMessage.to = username
        customMessage.chatType = EMMessage.ChatType.Chat
        return customMessage
    }

    fun insertMessage(message: EMMessage){
        // 将消息插入到指定会话中。
        val conversation = EMClient.getInstance().chatManager().getConversation(message.to)
        conversation.insertMessage(message)
        // 直接插入消息。
        message.isDelivered=true
        message.setStatus(EMMessage.Status.SUCCESS)
        EMClient.getInstance().chatManager().saveMessage(message)
    }

    fun startMessageListener(){
        iLog("用户${EMClient.getInstance().currentUser}注册消息监听")
        EMClient.getInstance().chatManager().removeMessageListener(msgListener)
        EMClient.getInstance().chatManager().addMessageListener(msgListener)

        EMClient.getInstance().chatManager().removeConversationListener(conversationsListener)
        EMClient.getInstance().chatManager().addConversationListener(conversationsListener)
    }
}