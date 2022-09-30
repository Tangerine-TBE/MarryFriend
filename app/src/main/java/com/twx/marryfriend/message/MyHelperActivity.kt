package com.twx.marryfriend.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.hyphenate.easeui.constants.EaseConstant

class MyHelperActivity:ImChatActivity() {
    companion object{
        fun getIntent(context: Context): Intent {
            val intent= Intent(context,MyHelperActivity::class.java)
            intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID,ImConversationFragment.MY_HELPER_ID)
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatSetting.visibility=View.GONE
//        layout_chat.init(ImConversationFragment.MY_HELPER_ID, EaseConstant.CHATTYPE_SINGLE)
//        layout_chat.chatInputMenu.visibility= View.GONE
//        layout_chat.setBackgroundColor(Color.parseColor("#FFF5F5F5"))
//        layout_chat.loadDefaultData()


//        helperService.setOnClickListener {
//            iLog("客服")
//        }
//        helperFeedback.setOnClickListener {
//            startActivity(Intent(this, SuggestionActivity::class.java))
//        }
    }
}