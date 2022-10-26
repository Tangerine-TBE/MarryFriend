package com.twx.marryfriend.message

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeim.R
import com.hyphenate.easeim.section.chat.fragment.ChatFragment
import com.hyphenate.easeui.modules.chat.EaseChatPrimaryMenu
import com.hyphenate.easeui.modules.chat.interfaces.EaseChatPrimaryMenuListener
import com.message.ImMessageManager
import com.message.chat.CustomEvent
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.dialog.ReChargeCoinDialog
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.viewmodel.ImChatViewModel
import com.twx.marryfriend.recommend.RecommendViewModel
import com.xyzz.myutils.SPUtil
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImChatFragment: ChatFragment() {
    companion object{
        private const val IS_SEND_OPEN_VIP="is_send_open_vip"
        fun putSendOpenVipMsg(conversationId:String){
            SPUtil.instance.putBoolean(IS_SEND_OPEN_VIP+conversationId,true)
        }
        fun isSendOpenVipMsg(conversationId:String):Boolean{
            return SPUtil.instance.getBoolean(IS_SEND_OPEN_VIP+conversationId,false)
        }

        private const val IS_SEND_UPLOAD_HEAD="IS_SEND_UPLOAD_HEAD"
        fun putUploadHeadMsg(conversationId:String){
            SPUtil.instance.putBoolean(IS_SEND_UPLOAD_HEAD+conversationId,true)
        }
        fun isUploadHeadMsg(conversationId:String):Boolean{
            return SPUtil.instance.getBoolean(IS_SEND_UPLOAD_HEAD+conversationId,false)
        }
    }

    private val recommendViewModel by lazy {
        ViewModelProvider(this).get(RecommendViewModel::class.java)
    }
    private val coinInsufficientDialog by lazy {
        ReChargeCoinDialog(requireActivity())
    }
    private val isMyHelper by lazy {
        conversationId==ImMessageManager.MY_HELPER_ID
    }
    private val imChatViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ImChatViewModel::class.java)
    }
    private var sensitiveWords:List<String>?=null

    override fun initView() {
        super.initView()
        isSendSuperVip= isSendOpenVipMsg(conversationId?:return)
        isSendHead=isUploadHeadMsg(conversationId?:return)
        ImMessageManager.insertSecurityMessage(conversationId?:return)//发送安全提示
        lifecycleScope.launch {
            sensitiveWords=imChatViewModel.getSensitiveWords()
        }

        val easeChatPrimaryMenuListener= EaseChatPrimaryMenu::class.java.getDeclaredField("listener").also {
            it.isAccessible=true
        }.get(chatLayout.chatInputMenu.primaryMenu) as? EaseChatPrimaryMenuListener
        chatLayout.chatInputMenu.primaryMenu.setEaseChatPrimaryMenuListener(object : EaseChatPrimaryMenuListener{
            override fun onSendBtnClicked(content: String?) {
                iLog("点击了发送")
                val pcontent=
                if (sensitiveWords?.any { content?.contains(it)==true }==true){
                    toast("消息包含非法内容")
                    chatLayout.chatInputMenu.primaryMenu.editText.setText(content)
                    chatLayout.chatInputMenu.primaryMenu.editText.setSelection(content?.length?:0)
                    content+"(违规)"
                }else{
                    easeChatPrimaryMenuListener?.onSendBtnClicked(content)
                    content
                }
                lifecycleScope.launch {
                    imChatViewModel.putMessage(conversationId,pcontent?:return@launch)
                }
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
        polling()
    }

    private var isSendSuperVip= false
    private var isSendHead=false
    private fun polling(){
        if (isMyHelper){
            return
        }
        if (!UserInfo.isSuperVip()&&!isSendSuperVip){
            lifecycleScope.launch {
                while (!UserInfo.isSuperVip()&&!isSendSuperVip){
                    val ms= ImMessageManager.getHistoryMessage(conversationId?:return@launch,10).take(3)
                    if (ms.firstOrNull()?.type!= EMMessage.Type.CUSTOM&&ms.all { it.from== UserInfo.getUserId() }){
                        ImMessageManager.getCustomMessage(conversationId?:return@launch,
                            CustomEvent.openSuperVip)?.also {
                            ImMessageManager.insertMessage(it)
                        }
                        isSendSuperVip=true
                        putSendOpenVipMsg(conversationId)
                    }
                    delay(2800)
                }
            }
        }
        if (!UserInfo.isHaveHeadImage()&&!isSendHead){
            lifecycleScope.launch {
                while (!UserInfo.isHaveHeadImage()&&!isSendHead){
                    val ms= ImMessageManager.getHistoryMessage(conversationId?:return@launch,10).take(3)
                    if (ms.firstOrNull()?.type!= EMMessage.Type.CUSTOM&&ms.all { it.from== UserInfo.getUserId() }){
                        ImMessageManager.getCustomMessage(conversationId?:return@launch,
                            CustomEvent.upload_head)?.also {
                            ImMessageManager.insertMessage(it)
                        }
                        isSendHead=true
                        putUploadHeadMsg(conversationId)
                    }
                    delay(3200)
                }
            }
        }
    }

    override fun onClickSendFlower() {
        lifecycleScope.launch {
            try {
                recommendViewModel.superLike(conversationId?.toIntOrNull()?:return@launch){
                    coinInsufficientDialog.show()
                }.also {
                    if (it.code==200){
                        super.onClickSendFlower()
                    }else{
                        toast(it.msg)
                    }
                }
            }catch (e:Exception){
                toast(e.message)
            }
        }
    }

    override fun onChatExtendMenuItemClick(view: View, itemId: Int) {
        if (itemId==com.hyphenate.easeui.R.id.extend_item_location){
            if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                toast("发送位置需要位置权限")
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),123)
                return
            }
        }
        super.onChatExtendMenuItemClick(view, itemId)
    }

    override fun onUserAvatarClick(username: String) {
        startActivity(FriendInfoActivity.getIntent(requireContext(), username.toIntOrNull()?:return))
    }

    override fun getAvatarDefaultSrc(): Drawable? {
        return ContextCompat.getDrawable(requireContext(), UserInfo.getReversedDefHeadImage())
    }

    override fun resetChatExtendMenu() {
        super.resetChatExtendMenu()
        val chatExtendMenu = chatLayout.chatInputMenu.chatExtendMenu
        if (conversationId!=ImMessageManager.MY_HELPER_ID){
            chatExtendMenu.registerMenuItem(
                R.string.send_flower,
                R.mipmap.ic_item_send_flowers,
                R.id.extend_item_send_flower
            )
        }
    }
}