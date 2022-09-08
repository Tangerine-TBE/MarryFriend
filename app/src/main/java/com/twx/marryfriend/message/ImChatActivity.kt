package com.twx.marryfriend.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.hyphenate.easeim.DemoHelper
import com.hyphenate.easeim.common.constant.DemoConstant
import com.hyphenate.easeim.section.chat.activity.ChatActivity
import com.hyphenate.easeim.section.chat.fragment.ChatFragment
import com.hyphenate.easeui.constants.EaseConstant
import com.twx.marryfriend.ImHelper
import com.twx.marryfriend.R
import com.twx.marryfriend.friend.FriendInfoActivity
import com.xyzz.myutils.createDialog
import com.xyzz.myutils.show.toast

class ImChatActivity: ChatActivity() {
    companion object{
        private const val NICKNAME_KEY="nickname_key"
        private const val HEAD_IMAGE_KEY="head_image_key"
        private const val IS_REAL_NAME_KEY="head_image_key"

        /**
         * @param conversationId 对方id
         * @param nickname 昵称
         * @param headImage 头像
         * @param isRealName 是否实名
         */
        fun getIntent(context: Context, conversationId: String, nickname:String?=null, headImage:String?=null, isRealName:Boolean): Intent {
            val intent= Intent(context,ImChatActivity::class.java)
            intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID,conversationId)
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE)

            intent.putExtra(NICKNAME_KEY,nickname?:conversationId)
            intent.putExtra(HEAD_IMAGE_KEY,headImage)
            intent.putExtra(IS_REAL_NAME_KEY,isRealName)
            return intent
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
    private val chatSettingDialog by lazy {
        createDialog(R.layout.dialog_chat_setting)
            .also {dialog->
                dialog.window?.setGravity(Gravity.BOTTOM)
                dialog.findViewById<View>(R.id.closeDialog).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.seeTaInfo).setOnClickListener {
                    startActivity(FriendInfoActivity.getIntent(this,conversationId?.toIntOrNull()?:return@setOnClickListener))
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.setTop).setOnClickListener {
                    toast("置顶")
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.blockFriends).setOnClickListener {
                    toast("屏蔽好友")
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.report).setOnClickListener {
                    toast("举报")
                    dialog.dismiss()
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
            chatSettingDialog.show()
        }
    }
}