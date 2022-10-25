package com.twx.marryfriend.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ViewSwitcher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.KeyboardUtils
import com.hyphenate.easeim.DemoHelper
import com.hyphenate.easeim.common.constant.DemoConstant
import com.hyphenate.easeim.section.chat.activity.ChatActivity
import com.hyphenate.easeim.section.chat.fragment.ChatFragment
import com.hyphenate.easeui.constants.EaseConstant
import com.message.ImUserInfoService
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.dialog.ChatSettingDialog
import com.twx.marryfriend.message.viewmodel.ImChatViewModel
import com.xyzz.myutils.show.toast
import kotlinx.coroutines.launch

//R.drawable.ease_default_avatar
//R.layout.demo_activity_chat
open class ImChatActivity: ChatActivity() {
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
            intent.putExtra(IS_REAL_NAME_KEY,/*isRealName?:ext?.isRealName*/true)
            return intent
        }
        fun getConversationId(intent: Intent?):String?{
            return intent?.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID)
        }
    }
    private val isRealName by lazy {
//        ImHelper.getUserInfo(conversationId?:return@lazy false)?.isRealName?:false
        true
    }
    private val friendRealName by lazy {
        findViewById<View>(com.hyphenate.easeim.R.id.friendRealName)
    }
    protected val chatSetting by lazy {
        findViewById<View>(com.hyphenate.easeim.R.id.chatSetting)
    }
    private val contentViewSwitcher by lazy {
        findViewById<ViewSwitcher>(com.hyphenate.easeim.R.id.contentViewSwitcher)
    }
    private val chatView by lazy {
        findViewById<View>(com.hyphenate.easeim.R.id.chatView)
    }
    private val interdictionView by lazy {
        findViewById<View>(com.hyphenate.easeim.R.id.interdictionView)
    }
    private val reportUser by lazy {
        findViewById<View>(com.hyphenate.easeim.R.id.reportUser)
    }
    private val imChatViewModel by lazy {
        ViewModelProvider(this).get(ImChatViewModel::class.java)
    }
    private var isBlock=false
    private var isInterdiction=false
    private val chatSettingDialog by lazy {
        val cid=conversationId
        ChatSettingDialog(this,cid?:return@lazy null)
            .also { chatSettingDialog1 ->
                val blockText=chatSettingDialog1.getBlockFriendText()
                chatSettingDialog1.setOnShowListener {
                    if (isBlock){
                        blockText.text="取消屏蔽"
                    }else{
                        blockText.text="屏蔽"
                    }
                }
                chatSettingDialog1.setBlockFriendsListener {
                    lifecycleScope.launch{
                        if(isBlock){
                            try {
                                imChatViewModel.removeBlockList(conversationId?:return@launch)
                                toast("取消屏蔽成功")
                                blockText.text="屏蔽"
                                isBlock=false
                            }catch (e:Exception){
                                toast(e.message)
                            }
                        }else{
                            try {
                                imChatViewModel.addBlockList(conversationId?:return@launch)
                                toast("屏蔽成功")
                                blockText.text="取消屏蔽"
                                isBlock=true
                            }catch (e:Exception){
                                toast(e.message)
                            }
                        }
                    }
                }
            }
    }

    private val chatFragment by lazy {
        ImChatFragment()
            .also {
                val bundle = Bundle()
                bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId)
                bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType)
                bundle.putString(DemoConstant.HISTORY_MSG_ID, historyMsgId)
                bundle.putBoolean(EaseConstant.EXTRA_IS_ROAM, DemoHelper.instance.model.isMsgRoaming)
                it.arguments = bundle
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
                try {
                    val aaa=imChatViewModel.getOurRelationship(conversationId?:return@launch)
                    isBlock= aaa.woPingBiTa
                    isInterdiction=aaa.ta_blacklist?.isInterdiction()?:false
                }catch (e:Exception){
                    isBlock=false
                    isInterdiction=false
                }

            if (isInterdiction){
                KeyboardUtils.hideSoftInput(this@ImChatActivity)
            }
            if (isInterdiction.xor(contentViewSwitcher.currentView==interdictionView)){
                contentViewSwitcher.showNext()
            }
        }
    }

    override fun getChatFragment(): ChatFragment {
        return chatFragment
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
        reportUser.setOnClickListener {
            startActivity(IntentManager.getReportIntent(this,conversationId?.toIntOrNull()?:return@setOnClickListener))
        }
    }

    override fun onChatError(code: Int, errorMsg: String?) {
        if(code==210){
            toast("你已经被对方屏蔽")
        }else if(code==501){
            toast("消息包含非法内容")
        }else{
            super.onChatError(code, errorMsg)
        }
    }
}