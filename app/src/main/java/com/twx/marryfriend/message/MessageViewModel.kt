package com.twx.marryfriend.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.message.conversations.ImMessageManager
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.message.model.ConversationsItemModel
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.show.iLog
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class MessageViewModel:ViewModel() {
    suspend fun getAllConversations()=suspendCoroutine<List<ConversationsItemModel>>{ continuation->
        viewModelScope.launch {
            ImMessageManager.getAllConversations().map {
                ConversationsItemModel(it.userId,it.conversationType)
                    .apply {
                        this.unReaderCount=it.unReaderCount
                        this.lastTime=it.lastTime
                        this.lastMassage=it.lastMassage
                        this.msgType=it.conversationType
//                        val userInfo=getUserInfo()
                        this.userImage
                        this.nickname="这是昵称${Random.nextInt()}"
                        this.isRealName=Random.nextBoolean()
                        this.age=Random.nextInt()
                        this.occupation
                        this.education
                    }
            }.also {
                continuation.resume(it)
            }
        }
    }

    private suspend fun getUserInfo()=suspendCoroutine<Int>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/LoginRegister/getBase"
        val map= mapOf(
            "user_id" to UserInfo.getUserId())
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                coroutine.resume(0)
                iLog(response)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }
}