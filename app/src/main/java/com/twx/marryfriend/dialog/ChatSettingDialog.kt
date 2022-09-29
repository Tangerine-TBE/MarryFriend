package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import com.hyphenate.chat.EMClient
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo
import com.hyphenate.easeui.utils.EaseCommonUtils
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.vip.SVipGifEnum
import com.twx.marryfriend.friend.FriendInfoActivity
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.dialog_chat_setting.*

class ChatSettingDialog(context: Context,private val conversationId:String): Dialog(context) {

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_chat_setting)
    }
    private val emConversation by lazy {
        EMClient.getInstance().chatManager().getConversation(conversationId)
    }
    private val conversationInfo by lazy {
        val info = EaseConversationInfo()
        info.info = emConversation
        val extField = emConversation.extField
        val lastMsgTime = emConversation.lastMessage.msgTime
        if (!extField.isNullOrBlank() && EaseCommonUtils.isTimestamp(extField)) {
            info.isTop = true
            val makeTopTime = extField.toLong()
            if (makeTopTime > lastMsgTime) {
                info.timestamp = makeTopTime
            } else {
                info.timestamp = lastMsgTime
            }
        } else {
            info.timestamp = lastMsgTime
        }
        info
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        closeDialog.setOnClickListener {
            dismiss()
        }
        seeTaInfo.setOnClickListener {
            context.startActivity(FriendInfoActivity.getIntent(
                context,
                conversationId.toIntOrNull()?:return@setOnClickListener
            ))
            dismiss()
        }
        if (conversationInfo.isTop){
            setTop.text="取消置顶"
        }else{
            setTop.text="置顶"
        }
        setTop.setOnClickListener {
            if (UserInfo.isSuperVip()){
                if (conversationInfo.isTop){
                    setTop.text="置顶"
                    conversationInfo.isTop=false
                    emConversation.extField=""
                    toast(context,"取消置顶成功")
                }else{
                    setTop.text="取消置顶"
                    conversationInfo.isTop=true
                    emConversation.extField=System.currentTimeMillis().toString()
                    toast(context,"置顶成功")
                }
            }else{
                context.startActivity(IntentManager.getSuperVipIntent(context, sVipGifEnum = SVipGifEnum.TopMessage))
            }
            dismiss()
        }
        report.setOnClickListener {
            dismiss()
            context.startActivity(IntentManager.getReportIntent(context,conversationId?.toIntOrNull()?:return@setOnClickListener))
        }
    }

    fun getBlockFriendText():TextView{
        return blockFriends
    }

    fun setBlockFriendsListener(action:()->Unit){
        blockFriends.setOnClickListener {
            action.invoke()
            dismiss()
        }
    }
}