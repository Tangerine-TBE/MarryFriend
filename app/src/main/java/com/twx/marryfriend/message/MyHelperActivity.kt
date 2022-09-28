package com.twx.marryfriend.message

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hyphenate.easeui.constants.EaseConstant
import com.twx.marryfriend.R
import com.twx.marryfriend.set.feedback.SuggestionActivity
import com.xyzz.myutils.show.iLog
import kotlinx.android.synthetic.main.activity_my_helper.*

class MyHelperActivity:AppCompatActivity(R.layout.activity_my_helper) {
    companion object{
        fun getIntent(context: Context):Intent{
            val intent=Intent(context,MyHelperActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout_chat.init(ImConversationFragment.MY_HELPER_ID, EaseConstant.CHATTYPE_SINGLE)
        layout_chat.chatInputMenu.visibility= View.GONE
        layout_chat.setBackgroundColor(Color.parseColor("#FFF5F5F5"))
        layout_chat.loadDefaultData()

        helperSetting.setOnClickListener {
            iLog("设置")
        }
        helperService.setOnClickListener {
            iLog("客服")
        }
        helperFeedback.setOnClickListener {
            startActivity(Intent(this, SuggestionActivity::class.java))
        }
    }
}