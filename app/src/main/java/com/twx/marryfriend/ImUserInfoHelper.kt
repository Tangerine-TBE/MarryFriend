package com.twx.marryfriend

import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeim.common.livedatas.LiveDataBus
import com.hyphenate.easeim.section.base.WebViewActivity
import com.hyphenate.easeui.utils.EaseUserUtils
import com.message.*
import com.message.chat.CustomEvent
import com.message.custom.IImEventListener
import com.message.custom.ImCustomEventListenerManager
import com.message.custom.MyHelperAdapterDelegate
import com.twx.marryfriend.bean.Sex
import com.twx.marryfriend.bean.vip.SVipGifEnum
import com.twx.marryfriend.message.ConversationViewModel
import com.twx.marryfriend.message.model.ConversationsItemModel
import com.xyzz.myutils.SPUtil
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.toast
import com.xyzz.myutils.show.wLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    fun init(){
        val userId=UserInfo.getUserId()
        ImUserInfoService.setUserInfo(ImUserInfoService.ImUserInfo(userId?:return,UserInfo.getNickname(),UserInfo.getImgHead()))
        GlobalScope.launch {
            iLog("首次启动设置环信用户信息")
            val conversations=try {
                val s=messageViewModel.getAllConversations()
                iLog("通过接口获取环信用户信息,${s?.map { it.nickname }}")
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
                ImUserInfoService.ImUserInfo(it.conversationId,it.nickname,it.userImage,
                    ImUserInfoService.Ext(it.age,it.isRealName,it.isVip,it.isSuperVip,it.location,it.occupation,it.education,it.isMutualLike,it.isFlower))
            }
            result?.also {
                ImUserInfoService.setUserInfo(*it.toTypedArray())
            }
            withContext(Dispatchers.Main){
                login(userId)
            }
        }
        ImCustomEventListenerManager.addListener(object :IImEventListener{
            override fun click(view: View, event: CustomEvent, emMessage: EMMessage) {
                when(event){
                    CustomEvent.flower -> {

                    }
                    CustomEvent.security -> {
                        WebViewActivity.actionStart(view.context,"http://test.aisou.club/userManual/fraud.html")
                    }
                    CustomEvent.openSuperVip -> {
                        view.context.startActivity(IntentManager.getSuperVipIntent(view.context, sVipGifEnum = SVipGifEnum.TopMessage))
                    }
                    CustomEvent.upload_head -> {
                        view.context.startActivity(IntentManager.getUpHeadImageIntent(view.context))
                    }
                    CustomEvent.dazhaohu_str -> {
                        view.context.startActivity(IntentManager.getUpFillInGreetIntent(view.context))
                    }
                    CustomEvent.greetext_pass -> {
                        view.context.toast(event.title)
                    }
                    CustomEvent.greetext_fail -> {
                        view.context.startActivity(IntentManager.getUpFillInGreetIntent(view.context))
                    }
                    CustomEvent.putong_xihuan -> {
                        view.context.toast(event.title)
                    }
                    CustomEvent.touxiang_pass -> {
                        view.context.toast(event.title)
                    }
                    CustomEvent.touxiang_fail -> {
                        view.context.startActivity(IntentManager.getUpHeadImageIntent(view.context))
                    }
                    CustomEvent.yuying_pass -> {
                        view.context.toast(event.title)
                    }
                    CustomEvent.yuying_fail -> {
                        GlobalScope.launch(Dispatchers.Main) {
                            view.context.startActivity(IntentManager.getUpFillInVoiceIntent(view.context))
                        }
                    }
                    CustomEvent.shiming_pass -> {
                        view.context.toast(event.title)
                    }
                    CustomEvent.shiming_fail -> {
                        view.context.startActivity(IntentManager.getUpRealNameIntent(view.context))
                    }
                    CustomEvent.xiangce_pass -> {
                        view.context.toast(event.title)
                    }
                    CustomEvent.xiangce_fail -> {
                        view.context.toast(event.title)
//                        view.context.startActivity(IntentManager.getUpRealNameIntent(view.context))
                    }
                    CustomEvent.shenghuo_pass -> {
                        view.context.toast(event.title)
                    }
                    CustomEvent.shenghuo_fail -> {
                        view.context.startActivity(IntentManager.getUpLifeIntent(view.context))
                    }
                    CustomEvent.dongtai_pass -> {
                        view.context.toast(event.title)
                    }
                    CustomEvent.dongtai_fail -> {
                        view.context.startActivity(IntentManager.getDynamicIntent(view.context))
                    }
                    CustomEvent.jubao_pass -> {
                        view.context.toast(event.title)
                    }
                    CustomEvent.jubao_fail -> {//举报
                        view.context.toast(event.title)
//                        view.context.startActivity(IntentManager.getReportIntent(view.context))
                    }
                    CustomEvent.interdi_pass -> {
                        view.context.toast(event.title)
                    }
                    CustomEvent.HELPER_VIP_EXPIRE -> {
                        view.context.startActivity(IntentManager.getVipIntent(view.context))
                    }
                }
            }
        })

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
                ImUserInfoService.ImUserInfo(itemModel.conversationId,itemModel.nickname,itemModel.userImage,

                    ImUserInfoService.Ext(itemModel.age,itemModel.isRealName,itemModel.isVip,itemModel.isSuperVip,itemModel.location,itemModel.occupation,itemModel.education,itemModel.isMutualLike,itemModel.isFlower).also {
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
        iLog("登录成功后设置信息")
        ImLoginHelper.login(userId,{easeUser->
            iLog("登录成功，开始刷新会话列表")
            MyHelperAdapterDelegate.sexAction={
                UserInfo.getOriginalUserSex()
            }
            ImInit.imLoginState.value=userId
            EaseUserUtils.setManDefHead(Sex.male.smallHead)
            EaseUserUtils.setWomanDefHead(Sex.woman.smallHead)
            EaseUserUtils.setSex(UserInfo.getOriginalUserSex())
            ImUserManager.connectionListener()
        },{code,message->
            iLog("登录失败,code=${code},message=${message}")
        })
    }
}