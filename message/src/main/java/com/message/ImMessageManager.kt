package com.message

import androidx.lifecycle.MutableLiveData
import com.hyphenate.EMConversationListener
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import com.hyphenate.exceptions.HyphenateException
import com.message.chat.CustomEvent
import com.message.conversations.ConversationType
import com.message.conversations.ConversationsBean
import com.xyzz.myutils.SPUtil
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog


//https://docs-im.easemob.com/im/android/basics/message
//http://sdkdocs.easemob.com/apidoc/android/chat3.0/annotated.html
object ImMessageManager {
    private const val SECURITY_MESSAGE_KEY="security_message_k_"
    const val MY_HELPER_ID="小秘书"//小秘书id

    val newMessageLiveData=MutableLiveData<List<EMMessage>?>()//收到新消息
    private val fromConversationRead=MutableLiveData<String>()
    private val messageRead=MutableLiveData<List<EMMessage>>()

    private val messageDelivered=MutableLiveData< MutableList<EMMessage>?>()

    private val msgListener by lazy {
        object : EMMessageListener {
            override fun onMessageReceived(messages: MutableList<EMMessage>?) {
                //收到消息
                iLog(messages?.firstOrNull()?.from,"收到消息,收到")
                newMessageLiveData.postValue(messages)
            }

            override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {
                messages?.forEach {
                    val ext=it.ext()
                    iLog("收到来自${it.from}的cmd消息，ext:${ext}")
                  /*  val kind=ext["kind"]
                    when(kind){
                        "dazhaohu_str","putong_xihuan"->{
                            insertSecurityMessage(it.conversationId())
                            // 将消息插入到指定会话中。
                            it.body=EMTextMessageBody(ext["str"].toString())
                            val conversation = EMClient.getInstance().chatManager().getConversation(it.conversationId())
                            conversation?.insertMessage(it)?:EMClient.getInstance().chatManager().saveMessage(it)
                        }
                        else->{

                        }
                    }*/
                }
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

    private val isSendSecurityMap by lazy {
        HashMap<String,Boolean>()
    }
    fun insertSecurityMessage(conversationId:String){
        if (conversationId==MY_HELPER_ID){
            return
        }
        val conversation=EMClient.getInstance().chatManager().getConversation(conversationId)
        val lastMsg=conversation?.lastMessage
        if ((conversation?.allMessages?.size?:0)<=1){
            val lastMsgBody=lastMsg?.body
            if (lastMsgBody is EMCustomMessageBody&&lastMsgBody.event()==CustomEvent.security.code){
                SPUtil.instance.putBoolean(SECURITY_MESSAGE_KEY+conversationId,true)
                isSendSecurityMap[conversationId]=true
                return
            }else{
                SPUtil.instance.putBoolean(SECURITY_MESSAGE_KEY+conversationId,false)
                isSendSecurityMap[conversationId]=false
            }
        }

        val isSendSecurity= isSendSecurityMap.get(conversationId)
        if (isSendSecurity==null){
            isSendSecurityMap[conversationId]=SPUtil.instance.getBoolean(SECURITY_MESSAGE_KEY+conversationId,false)
        }else if (isSendSecurity==true){
            return
        }

        if (isSendSecurity!=true){
            ImMessageManager.getCustomMessage(conversationId, CustomEvent.security)?.also { emMessage ->
                emMessage.msgTime=lastMsg?.msgTime?.let {
                    it-60*60*1000
                }?:(System.currentTimeMillis()-60*60*1000)
                emMessage.msgId=System.currentTimeMillis().toString()
                ImMessageManager.insertMessage(emMessage)
                SPUtil.instance.putBoolean(SECURITY_MESSAGE_KEY+conversationId,true)
                isSendSecurityMap[conversationId]=true
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

    fun getHistoryMessage(toChatUsername: String, pageSize: Int, msgId: String? = null):List<EMMessage>{
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
        return resultList.sortedBy {
            -it.msgTime
        }
    }

    fun getAllUnreadMessage():Int{
        return EMClient.getInstance()?.chatManager()?.unreadMessageCount?:0
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

    fun getFlowerMsg(username: String):EMMessage?{
        val customMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM)
// event为需要传递的自定义消息事件，比如礼物消息，可以设置event = "gift"
        val customBody = EMCustomMessageBody(CustomEvent.flower.code)
// params类型为Map<String, String>
//        customBody.params = params
        customMessage.addBody(customBody)
// to指另一方环信id（或者群组id，聊天室id）
        customMessage.setTo(username)
// 如果是群聊，设置chattype，默认是单聊
        customMessage.chatType = EMMessage.ChatType.Chat
        return customMessage
    }

    fun sendMsg(msg:EMMessage){
        EMClient.getInstance().chatManager().sendMessage(msg)
    }

    fun getCustomMessage(username: String, type: CustomEvent):EMMessage?{
        val customMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM)
        val customBody = EMCustomMessageBody(type.code)
        customMessage.addBody(customBody)
        customMessage.to = username
        customMessage.chatType = EMMessage.ChatType.Chat
        customMessage.msgTime=System.currentTimeMillis()
        customMessage.msgId=System.currentTimeMillis().toString()
        return customMessage
    }

    fun insertMessage(message: EMMessage){
        // 将消息插入到指定会话中。
        val conversation = EMClient.getInstance().chatManager().getConversation(message.to)
        // 直接插入消息。
        message.isDelivered=true
        message.setStatus(EMMessage.Status.SUCCESS)
        conversation?.insertMessage(message)?:EMClient.getInstance().chatManager().saveMessage(message)
    }

    fun startMessageListener(){
        iLog("用户${EMClient.getInstance().currentUser}注册消息监听")
        EMClient.getInstance().chatManager().removeMessageListener(msgListener)
        EMClient.getInstance().chatManager().addMessageListener(msgListener)

        EMClient.getInstance().chatManager().removeConversationListener(conversationsListener)
        EMClient.getInstance().chatManager().addConversationListener(conversationsListener)
    }
}