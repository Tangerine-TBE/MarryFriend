package com.twx.marryfriend.friend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class FriendInfoActivity:AppCompatActivity() {
    companion object{
        fun getIntent(context: Context,userId:Int):Intent{
            val intent=Intent(context,FriendInfoActivity::class.java)
            return intent
        }
    }
}