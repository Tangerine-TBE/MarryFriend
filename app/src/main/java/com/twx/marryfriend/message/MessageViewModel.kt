package com.twx.marryfriend.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.message.ImMessageManager
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.message.ConversationBean
import com.twx.marryfriend.bean.recommend.RecommendBean
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.message.model.ConversationsItemModel
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.show.iLog
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MessageViewModel:ViewModel() {
    private val gson by lazy {
        Gson()
    }

    suspend fun getAllConversations()=suspendCoroutine<List<ConversationsItemModel>>{ continuation->
        viewModelScope.launch {
            val allConversation= ImMessageManager.getAllConversations()
            val friendsInfo=try {
                getFriendsInfo(allConversation.map { it.userId }).data
            }catch (e:Exception){
                null
            }

            allConversation.map { conversationsBean ->
                val friendInfo=friendsInfo?.find {
                    it.id?.toString()==conversationsBean.userId
                }
                ConversationsItemModel(conversationsBean.userId,conversationsBean.conversationType)
                    .apply {
                        this.unReaderCount=conversationsBean.unReaderCount
                        this.lastTime=conversationsBean.lastTime
                        this.lastMassage=conversationsBean.lastMassage
                        this.msgType=conversationsBean.conversationType
//                        val userInfo=getUserInfo()
                        if (friendInfo!=null){
                            this.userImage=friendInfo.image_url
                            this.nickname=friendInfo.nick
                            this.isRealName=friendInfo.isRealName()
                            this.age=(friendInfo.age?:0)
                            this.occupation=friendInfo.occupation_str
                            this.education=RecommendBean.getEducationStr(friendInfo.education)?.label
                            this.location=friendInfo.work_city_str
                            this.isMutualLike=true
                        }
                    }
            }.also { list ->
                continuation.resume(list)
            }
        }
    }

    private suspend fun getFriendsInfo(userArray:List<String>)=suspendCoroutine<ConversationBean>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/TrendsNotice/huanxinChatList"
        val map= mapOf(
            "user_id" to UserInfo.getUserId(),
            "uid_array" to gson.toJson(userArray)
            )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                coroutine.resume(gson.fromJson(response,ConversationBean::class.java))
                iLog(response)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }
}