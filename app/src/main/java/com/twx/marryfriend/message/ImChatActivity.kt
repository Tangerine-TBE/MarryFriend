package com.twx.marryfriend.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hyphenate.chat.EMClient
import com.hyphenate.easeim.DemoHelper
import com.hyphenate.easeim.common.constant.DemoConstant
import com.hyphenate.easeim.section.chat.activity.ChatActivity
import com.hyphenate.easeim.section.chat.fragment.ChatFragment
import com.hyphenate.easeui.constants.EaseConstant
import com.message.ImUserInfoService
import com.twx.marryfriend.ImHelper
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.vip.SVipGifEnum
import com.twx.marryfriend.dialog.ChatSettingDialog
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.vip.VipActivity
import com.xyzz.myutils.createDialog
import com.xyzz.myutils.show.toast
import kotlinx.coroutines.launch

//R.drawable.ease_default_avatar
class ImChatActivity: ChatActivity() {
    companion object{
        private const val IS_REAL_NAME_KEY="head_image_key"

        /**
         * @param conversationId 对方id
         * @param isRealName 是否实名
         */
        fun getIntent(context: Context, conversationId: String, isRealName: Boolean? = null): Intent {
            val intent= Intent(context,ImChatActivity::class.java)
            intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID,conversationId)
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE)

            val ext= ImUserInfoService.getExt(conversationId)
            intent.putExtra(IS_REAL_NAME_KEY,isRealName?:ext?.isRealName)
            return intent
        }
        fun getConversationId(intent: Intent?):String?{
            return intent?.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID)
        }
    }
    private val isRealName by lazy {
        ImHelper.getUserInfo(conversationId?:return@lazy false)?.isRealName?:false
    }
    private val friendRealName by lazy {
        findViewById<View>(R.id.friendRealName)
    }
    private val chatSetting by lazy {
        findViewById<View>(R.id.chatSetting)
    }
    private val imChatViewModel by lazy {
        ViewModelProvider(this).get(ImChatViewModel::class.java)
    }
    private val chatSettingDialog by lazy {
        ChatSettingDialog(this,conversationId?:return@lazy null)
            .also {
                it.setBlockFriendsListener {
                    lifecycleScope.launch{
                        try {
                            imChatViewModel.addBlockList(conversationId?:return@launch)
                            toast("屏蔽成功")
                        }catch (e:Exception){
                            toast(e.message)
                        }
                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getChatFragment(): ChatFragment {
        return ImChatFragment()
            .also {
                val bundle = Bundle()
                bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId)
                bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType)
                bundle.putString(DemoConstant.HISTORY_MSG_ID, historyMsgId)
                bundle.putBoolean(EaseConstant.EXTRA_IS_ROAM, DemoHelper.instance.model.isMsgRoaming)
                it.arguments = bundle
            }
    }

    override fun initListener(){
        super.initListener()
        if (!isRealName){
            friendRealName.setOnClickListener {
                toast("发送小助手消息,实名认证")
            }
        }else{
            friendRealName.visibility=View.GONE
        }
        chatSetting.setOnClickListener {
            chatSettingDialog?.show()
        }
    }

    override fun onChatError(code: Int, errorMsg: String?) {
        if(code==210){
            toast("你已经被对方屏蔽")
        }else{
            super.onChatError(code, errorMsg)
        }
    }
}