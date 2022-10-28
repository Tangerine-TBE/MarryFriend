package com.twx.marryfriend.message.viewmodel

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
import com.xyzz.myutils.show.wLog
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ConversationViewModel:ViewModel() {
    private val gson by lazy {
        Gson()
    }

    suspend fun getAllConversations()=suspendCoroutine<List<ConversationsItemModel>?>{ continuation->
        viewModelScope.launch {
            val allConversation= ImMessageManager.getAllConversations().sortedBy {
                -it.lastTime
            }
            getConversationsInfo(allConversation.map { it.conversationId }).also {
                continuation.resume(it)
            }
        }
    }

    suspend fun getConversationsInfo(ids:List<String>)=suspendCoroutine<List<ConversationsItemModel>?>{ continuation->
        viewModelScope.launch {
            val friendsInfo=try {
                getFriendsInfo(ids).data
            }catch (e:Exception){
                null
            }
            friendsInfo?.mapNotNull {
                val id=it.user_id?.toString()
                if (id==null){
                    null
                }else{
                    ConversationsItemModel(id)
                        .apply {
                            this.age=(it.age?:0)
                            this.isSuperVip=it.isSuperVip()
                            this.isVip=it.isVip()
                            this.image_url=it.image_url
                            this.nick=it.nick
                            this.isRealName=it.isRealName()
                            this.occupation_str=it.occupation_str
                            this.education=RecommendBean.getEducationStr(it.education)?.label
                            this.location=it.work_city_str
                            this.isMutualLike=it.isMutualLike()
                            this.isFlower=it.isFlower()
                            this.blacklist_permanent=it.blacklist_permanent?:0
                            this.blacklist_close_time=it.blacklist_close_time
                            this.blacklist_status=it.blacklist_status
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
            "uid_array" to gson.toJson(userArray.filter { it.toIntOrNull()!=null })
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
                if (jsonObject.get("data").toString()=="[]"){
                    coroutine.resume(MutualLikeBean())
                }else{
                    coroutine.resume(gson.fromJson(jsonObject.getJSONObject("data").toString(), MutualLikeBean::class.java))
                }
            }catch (e:Exception){
                wLog(e.stackTraceToString())
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    suspend fun getFollowCountAndImg()=suspendCoroutine<Pair<Int,String>?>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/TrendsNotice/focousCountImg"
        val map= mapOf(
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录")))
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val data=jsonObject.getJSONObject("data")
                val total=data.getInt("total")
                if (total<=0){
                    coroutine.resume(null)
                }else{
                    coroutine.resume(total to data.getString("image"))
                }
                iLog(response)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }
}