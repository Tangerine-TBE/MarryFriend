package com.twx.marryfriend.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.KeyboardUtils
import com.message.ImMessageManager
import com.message.conversations.ConversationType
import com.twx.marryfriend.R
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity:AppCompatActivity(R.layout.activity_chat) {
    companion object{
        private const val FRIEND_ID_KEY="friend_id_key"
        private const val NICKNAME_KEY="nickname_key"
        private const val HEAD_IMAGE_KEY="head_image_key"
        fun getIntent(context: Context, friendId: String,nickname:String?,headImage:String?):Intent{
            val intent=Intent(context,ChatActivity::class.java)
            intent.putExtra(FRIEND_ID_KEY,friendId)
            intent.putExtra(NICKNAME_KEY,nickname?:friendId)
            intent.putExtra(HEAD_IMAGE_KEY,headImage)
            return intent
        }
    }
    private val friendId by lazy {
        intent?.getStringExtra(FRIEND_ID_KEY)
    }
    private val nickname by lazy {
        intent?.getStringExtra(NICKNAME_KEY)
    }
    private val headImage by lazy {
        intent?.getStringExtra(HEAD_IMAGE_KEY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KeyboardUtils.fixAndroidBug5497(this)
        chatRecyclerView.layoutManager=LinearLayoutManager(this, LinearLayoutManager.VERTICAL,true)
        chatRecyclerView
    }
}