package com.twx.marryfriend.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.KeyboardUtils
import com.twx.marryfriend.R

class ChatActivity:AppCompatActivity(R.layout.activity_chat) {
    companion object{
        private const val FRIEND_ID_KEY="friend_id_key"
        fun getIntent(context: Context,friendId:String):Intent{
            val intent=Intent(context,ChatActivity::class.java)
            intent.putExtra(FRIEND_ID_KEY,friendId)
            return intent
        }
    }
    private val friendId by lazy {
        intent?.getStringExtra(FRIEND_ID_KEY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KeyboardUtils.fixAndroidBug5497(this)

    }
}