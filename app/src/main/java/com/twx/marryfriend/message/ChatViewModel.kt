package com.twx.marryfriend.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessageBody
import com.message.ImMessageManager
import com.message.chat.*
import com.twx.marryfriend.message.model.ChatItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChatViewModel:ViewModel() {
    companion object{
        private const val PAGE_SIZE=20
    }
    private var msgId:String?=null
    var page=1

    suspend fun getHistoryMessage(username:String)= suspendCoroutine<List<ChatItemModel<Message<out EMMessageBody>>>>{continuation->
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ImMessageManager.getHistoryMessage(username,EMConversation.EMConversationType.Chat,
                    PAGE_SIZE,msgId).also { list ->
                    msgId=list.lastOrNull()?.msgId
                    continuation.resume(list.map {
                        ChatItemModel(it)
                    })
                }
                page++
            }catch (e:Exception){
                continuation.resumeWithException(Exception("获取历史记录失败"))
            }

        }
    }
}