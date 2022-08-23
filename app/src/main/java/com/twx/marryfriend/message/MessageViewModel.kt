package com.twx.marryfriend.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.message.ImMessageManager
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.message.ConversationBean
import com.twx.marryfriend.bean.message.mutual.MutualLikeBean
import com.twx.marryfriend.bean.recommend.RecommendBean
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.message.model.ConversationsItemModel
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.show.iLog
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MessageViewModel:ViewModel() {
    private val gson by lazy {
        Gson()
    }

    suspend fun getAllConversations()=suspendCoroutine<List<ConversationsItemModel>>{ continuation->
        viewModelScope.launch {
            val allConversation= ImMessageManager.getAllConversations().sortedBy {
                -it.lastTime
            }
            val friendsInfo=try {
                getFriendsInfo(allConversation.map { it.conversationId }).data
            }catch (e:Exception){
                null
            }

            allConversation.map { conversationsBean ->
                val friendInfo=friendsInfo?.find {
                    it.id?.toString()==conversationsBean.conversationId
                }
                ConversationsItemModel(conversationsBean.conversationId,conversationsBean.conversationType)
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
                            this.isMutualLike=friendInfo.isLike()
                            this.isFlower=friendInfo.isFlower()
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
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
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

    suspend fun getMutualLike()=suspendCoroutine<MutualLikeBean>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/TrendsNotice/fiveLikeTotal"
        val map= mapOf(
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                coroutine.resume(gson.fromJson(jsonObject.getJSONObject("data").toString(), MutualLikeBean::class.java))
                iLog(response)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        }, mapOf(
            "page" to "1",
            "size" to "20"
        ))
    }
}