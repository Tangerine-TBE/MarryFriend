package com.twx.marryfriend

import android.content.Intent
import android.net.Uri
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hyphenate.chat.EMMessage
import com.message.ImLoginHelper
import com.message.ImUserInfoService
import com.message.chat.CustomMessage
import com.message.custom.IImEventListener
import com.message.custom.ImCustomEventListenerManager
import com.twx.marryfriend.message.MessageViewModel
import com.twx.marryfriend.message.model.ConversationsItemModel
import com.twx.marryfriend.vip.VipActivity
import com.xyzz.myutils.SPUtil
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.wLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ImHelper {
    private val messageViewModel by lazy {
        MessageViewModel()
    }
    private val imUiHelper by lazy {
        ImLoginHelper
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
        val str=SPUtil.instance.getString(IM_USER_INFO_K,null)?:return null
        val type = object : TypeToken<List<ConversationsItemModel>>() {}.type
        return gson.fromJson<List<ConversationsItemModel>?>(str, type).also {
            cacheValue=it
        }
    }

    fun init(){
        val userId=UserInfo.getUserId()

        ImUserInfoService.setUserInfo(ImUserInfoService.ImUserInfo(userId?:return,UserInfo.getNickname(),UserInfo.getImgHead()))
        GlobalScope.launch {
            val conversations=try {
                val s=messageViewModel.getAllConversations()
                SPUtil.instance.putString(IM_USER_INFO_K,gson.toJson(s))
                cacheValue=s
                s
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
            override fun click(view: View, event: CustomMessage.CustomEvent, emMessage: EMMessage) {
                when(event){
                    CustomMessage.CustomEvent.flower -> {

                    }
                    CustomMessage.CustomEvent.security -> {
                        view.context.startActivity(Intent(Intent.ACTION_VIEW).also {
                            it.data = Uri.parse("http://test.aisou.club/userManual/fraud.html")
                        })
                    }
                    CustomMessage.CustomEvent.openSuperVip -> {
                        view.context.startActivity(VipActivity.getIntent(view.context,1,emMessage.from?.toIntOrNull()))
                    }
                    CustomMessage.CustomEvent.upload_head -> {
                        view.context.startActivity(IntentManager.getUpHeadImageIntent(view.context))
                    }
                }
            }
        })
    }

    private fun login(userId:String){
        imUiHelper.login(userId,{easeUser->
            iLog("登录成功，开始刷新会话列表")
        },{code,message->
            iLog("登录失败,code=${code},message=${message}")
        })
    }
}