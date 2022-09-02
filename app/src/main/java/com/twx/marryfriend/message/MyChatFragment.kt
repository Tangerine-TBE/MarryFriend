package com.twx.marryfriend.message

import android.graphics.drawable.Drawable
import androidx.lifecycle.lifecycleScope
import com.hyphenate.easeim.section.chat.fragment.ChatFragment
import com.message.ImMessageManager
import com.message.chat.CustomMessage
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.friend.FriendInfoActivity
import com.xyzz.myutils.SPUtil
import kotlinx.coroutines.delay

class MyChatFragment: ChatFragment() {
    companion object{
        private const val IS_SEND_OPEN_VIP="is_send_open_vip"
        fun putSendOpenVipMsg(conversationId:String){
            SPUtil.instance.putBoolean(IS_SEND_OPEN_VIP+conversationId,true)
        }
        fun isSendOpenVipMsg(conversationId:String):Boolean{
            return SPUtil.instance.getBoolean(IS_SEND_OPEN_VIP+conversationId,false)
        }

        private const val IS_SEND_OPEN_VIP_UP="is_send_open_vip"
        fun putUploadHeadMsg(conversationId:String){
            SPUtil.instance.putBoolean(IS_SEND_OPEN_VIP_UP+conversationId,true)
        }
        fun isUploadHeadMsg(conversationId:String):Boolean{
            return SPUtil.instance.getBoolean(IS_SEND_OPEN_VIP_UP+conversationId,false)
        }
    }

    private var isVip =UserInfo.isVip()
    private var isSendSecurity=false
    private var isSendVip= false
    private var isSendHead=false
    private var isUploadHead=false

    override fun initView() {
        super.initView()
        isSendVip= isSendOpenVipMsg(conversationId?:return)
        isSendHead=isUploadHeadMsg(conversationId?:return)
        val msgs= ImMessageManager.getHistoryMessage(conversationId?:return,10)
        if (msgs.isEmpty()){//发送安全提示
            ImMessageManager.getCustomMessage(conversationId?:return, CustomMessage.CustomEvent.security)?.also {
//                ImMessageManager.insertMessage(it)
                sendMessage(it)
            }
            isSendSecurity=true
        }else{
            isSendSecurity=true
        }
//        ImMessageManager.getCustomMessage(conversationId?:return,CustomMessage.CustomEvent.upload_head)
    }

    override fun onResume() {
        super.onResume()
        if (!isVip&&!isSendVip){
            lifecycleScope.launchWhenResumed {
                while (!UserInfo.isVip()&&!isSendVip){
                    val ms= ImMessageManager.getHistoryMessage(conversationId?:return@launchWhenResumed,10).take(3)
                    if (ms.size==3&&ms.all { it.from== UserInfo.getUserId() }){
                        ImMessageManager.getCustomMessage(conversationId?:return@launchWhenResumed,
                            CustomMessage.CustomEvent.openVip)?.also {
                            ImMessageManager.insertMessage(it)
                        }
                        isSendVip=true
                        putSendOpenVipMsg(conversationId)
                    }
                    delay(3000)
                }
            }
        }else{
            isUploadHead=UserInfo.isHaveHeadImage()
            if (!isUploadHead&&!isSendHead){
                lifecycleScope.launchWhenResumed {
                    while (!UserInfo.isHaveHeadImage()){
                        val ms= ImMessageManager.getHistoryMessage(conversationId?:return@launchWhenResumed,10).take(3)
                        if (ms.size==3&&ms.all { it.from== UserInfo.getUserId() }){
                            ImMessageManager.getCustomMessage(conversationId?:return@launchWhenResumed,
                                CustomMessage.CustomEvent.upload_head)?.also {
                                ImMessageManager.insertMessage(it)
                            }
                            isSendHead=true
                            putUploadHeadMsg(conversationId)
                        }
                        delay(3000)
                    }
                }
            }
        }
    }

    override fun onUserAvatarClick(username: String) {
        startActivity(FriendInfoActivity.getIntent(requireContext(),username.toIntOrNull()?:return))
    }

    override fun getAvatarDefaultSrc(): Drawable? {
        return super.getAvatarDefaultSrc()
    }
}