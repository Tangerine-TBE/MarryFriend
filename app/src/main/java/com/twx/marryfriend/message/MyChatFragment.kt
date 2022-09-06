package com.twx.marryfriend.message

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.hyphenate.easeim.section.chat.fragment.ChatFragment
import com.hyphenate.easeui.modules.chat.EaseChatPrimaryMenu
import com.hyphenate.easeui.modules.chat.interfaces.EaseChatPrimaryMenuListener
import com.message.ImMessageManager
import com.message.chat.CustomMessage
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.friend.FriendInfoActivity
import com.xyzz.myutils.SPUtil
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.toast
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


        val easeChatPrimaryMenuListener= EaseChatPrimaryMenu::class.java.getDeclaredField("listener").also {
            it.isAccessible=true
        }.get(chatLayout.chatInputMenu.primaryMenu) as? EaseChatPrimaryMenuListener

        chatLayout.chatInputMenu.primaryMenu.setEaseChatPrimaryMenuListener(object : EaseChatPrimaryMenuListener{
            override fun onSendBtnClicked(content: String?) {
                easeChatPrimaryMenuListener?.onSendBtnClicked(content)
            }

            override fun onTyping(s: CharSequence?, start: Int, before: Int, count: Int) {
                easeChatPrimaryMenuListener?.onTyping(s, start, before, count)
            }

            override fun onPressToSpeakBtnTouch(v: View?, event: MotionEvent?): Boolean {
                val isPermissionGranted=ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED
                if (isPermissionGranted){
                    easeChatPrimaryMenuListener?.onPressToSpeakBtnTouch(v, event)
                }else{
                    toast("应用缺少录音权限无法发送语音消息")
                }
                iLog("触摸说话按钮")
                return isPermissionGranted
            }

            override fun onToggleVoiceBtnClicked() {
                easeChatPrimaryMenuListener?.onToggleVoiceBtnClicked()
                if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
                    AlertDialog.Builder(requireContext())
                        .setTitle("申请权限")
                        .setMessage("发送语音需要获取录音权限，是否允许应用申请录音权限？")
                        .setPositiveButton("允许"){_,_->
                            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),0)
                        }
                        .setNegativeButton("不允许"){_,_->

                        }
                        .show()
                }
            }

            override fun onToggleTextBtnClicked() {
                easeChatPrimaryMenuListener?.onToggleTextBtnClicked()
            }

            override fun onToggleExtendClicked(extend: Boolean) {
                easeChatPrimaryMenuListener?.onToggleExtendClicked(extend)
            }

            override fun onToggleEmojiconClicked(extend: Boolean) {
                easeChatPrimaryMenuListener?.onToggleEmojiconClicked(extend)
            }

            override fun onEditTextClicked() {
                easeChatPrimaryMenuListener?.onEditTextClicked()
            }

            override fun onEditTextHasFocus(hasFocus: Boolean) {
                easeChatPrimaryMenuListener?.onEditTextHasFocus(hasFocus)
            }

        })
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
        return ContextCompat.getDrawable(requireContext(), UserInfo.getReversedDefHeadImage())
    }
}