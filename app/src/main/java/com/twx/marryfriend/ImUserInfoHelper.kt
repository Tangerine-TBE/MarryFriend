package com.twx.marryfriend

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hyphenate.easeim.common.livedatas.LiveDataBus
import com.message.ImMessageManager
import com.message.ImUserInfoService
import com.twx.marryfriend.message.ConversationViewModel
import com.twx.marryfriend.message.model.ConversationsItemModel
import com.xyzz.myutils.SPUtil
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.wLog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ImUserInfoHelper {
    val observableNewMessage by lazy {
        LiveDataBus.get().with("im_user_info_update",Boolean::class.java)
    }
    private val messageViewModel by lazy {
        ConversationViewModel()
    }
    private val gson by lazy {
        Gson()
    }
    private const val IM_USER_INFO_K="im_user_info_k"
    var cacheValue:List<ConversationsItemModel>?=null
        private set

    fun getUserInfo(userId: String):ConversationsItemModel?{
        return cacheValue?.find {
            it.conversationId==userId
        }
    }

    private fun getCache():List<ConversationsItemModel>?{
        iLog("通过缓存获取环信用户信息")
        val str=SPUtil.instance.getString(IM_USER_INFO_K,null)?:return null
        val type = object : TypeToken<List<ConversationsItemModel>>() {}.type
        return gson.fromJson<List<ConversationsItemModel>?>(str, type).also {
            cacheValue=it
        }
    }

    /**
     * 我们用户登录后环信消息相关的初始化
     */
    fun initUserInfo(){
        val userId=UserInfo.getUserId()
        ImUserInfoService.setUserInfo(ImUserInfoService.ImUserInfo(userId?:return,UserInfo.getNickname(),UserInfo.getImgHead()))
        GlobalScope.launch {
            iLog("首次启动设置环信用户信息")
            val conversations=try {
                val s=messageViewModel.getAllConversations()
                iLog("通过接口获取环信用户信息,${s?.map { it.nick }}")
                if (!s.isNullOrEmpty()){
                    iLog("环信用户信息存入缓存")
                    SPUtil.instance.putString(IM_USER_INFO_K,gson.toJson(s))
                    cacheValue=s
                    s
                }else{
                    getCache()
                }
            }catch (e:Exception){
                wLog(e.stackTraceToString())
                getCache()
            }

            val result= conversations?.map {
                ImUserInfoService.ImUserInfo(it.conversationId,it.nick,it.image_url,
                    ImUserInfoService.Ext(it.age,it.isRealName,it.isVip,it.isSuperVip,it.location,it.occupation_str,it.education,it.isMutualLike,it.isFlower))
            }
            result?.also {
                ImUserInfoService.setUserInfo(*it.toTypedArray())
            }
        }

        ImMessageManager.newMessageLiveData.observeForever { list ->
            iLog("准备获取用户资料")
            list?.map {
                it.from
            }?.also {
                iLog("已准备好获取用户资料,${it}")
                addFriendInfo(it)
            }
        }
    }

    fun addFriendInfo(ids:List<String>){
        if (ids.isEmpty()){
            return
        }
        iLog("新增用户资料")
        ids.filter {
            ImUserInfoService.getUserNickName(it)==null
        }.also { list ->
            updateFriendInfo(list)
        }
    }

    fun refreshConversationsInfo(){
        val userId=UserInfo.getUserId()
        ImUserInfoService.setUserInfo(ImUserInfoService.ImUserInfo(userId?:return,UserInfo.getNickname(),UserInfo.getImgHead()))
        iLog("刷新环信用户信息")
        updateFriendInfo(ImMessageManager.getAllConversations().map { it.conversationId })
    }

    private fun updateFriendInfo(ids:List<String>){
        iLog("更新用户资料")
        iLog("已准备好获取用户资料,${ids}")
        GlobalScope.launch {
            iLog("开始获取用户资料,${ids}")
            val conversations=messageViewModel.getConversationsInfo(ids)
            val result= conversations?.map { itemModel ->
                ImUserInfoService.ImUserInfo(itemModel.conversationId,itemModel.nick,itemModel.image_url,

                    ImUserInfoService.Ext(itemModel.age,itemModel.isRealName,itemModel.isVip,itemModel.isSuperVip,itemModel.location,itemModel.occupation_str,itemModel.education,itemModel.isMutualLike,itemModel.isFlower).also {
                        it.blacklist_permanent=itemModel.blacklist_permanent
                        it.blacklist_close_time=itemModel.blacklist_close_time
                        it.blacklist_status=itemModel.blacklist_status
                    })
            }?:return@launch
            result.also { resultList ->
                iLog("获取用户资料成功,${resultList.map { it.userId }}")
                ImUserInfoService.setUserInfo(*resultList.toTypedArray())
//                    val fff= resultList.mapNotNull { it.userId.toIntOrNull() }.toIntArray()
                observableNewMessage.postValue(true)
            }
        }
    }

    private fun login(userId:String){
//        ImLoginHelper.login(userId,{easeUser->
//            // 获取华为 HMS 推送 token
//            HMSPushHelper.getInstance().getHMSToken(BaseConstant.application)
//            iLog("登录成功，开始刷新会话列表")
//            MyHelperAdapterDelegate.sexAction={
//                UserInfo.getOriginalUserSex()
//            }
//            ImInit.imLoginState.value=userId
//            EaseUserUtils.setManDefHead(Sex.male.smallHead)
//            EaseUserUtils.setWomanDefHead(Sex.woman.smallHead)
//            EaseUserUtils.setSex(UserInfo.getOriginalUserSex())
//            ImUserManager.connectionListener()
//        },{code,message->
//            iLog("登录失败,code=${code},message=${message}")
//        })
    }
}