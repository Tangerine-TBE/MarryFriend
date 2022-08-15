package com.twx.marryfriend.message.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.*

class ChatImageRedFlowerItemView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0):
    FrameLayout(context,attributeSet,defSty) {
    private val dataBindingView by lazy {
        if (true){
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(context), R.layout.item_my_chat_red_flower_msg,this,false)
        }else{
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(context), R.layout.item_friend_chat_red_flower_msg,this,false)
        }
    }
}