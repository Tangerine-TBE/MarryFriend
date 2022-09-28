package com.twx.marryfriend.message

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

@Deprecated("废弃,使用ImChatActivity")
class ChatActivity:AppCompatActivity() {
    companion object{
        fun getIntent(context: Context,conversationId: String,isRealName: Boolean):Intent{
//            val intent=Intent(context,ChatActivity::class.java)
//            intent.putExtra(FRIEND_ID_KEY,conversationId)
//            intent.putExtra(NICKNAME_KEY,nickname?:conversationId)
//            intent.putExtra(HEAD_IMAGE_KEY,headImage)
//            intent.putExtra(IS_REAL_NAME_KEY,isRealName)
//
//            ImMessageManager.ackConversationRead(conversationId)
//            return intent
            return ImChatActivity.getIntent(context, conversationId, isRealName)
        }
    }
}