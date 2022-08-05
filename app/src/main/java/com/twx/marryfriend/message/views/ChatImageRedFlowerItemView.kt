package com.twx.marryfriend.message.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.*

class ChatImageRedFlowerItemView constructor(parent: ViewGroup):BaseDataBindingView {
    private val dataBindingView by lazy {
        if (true){
            DataBindingUtil.inflate<ItemMyChatRedFlowerMsgBinding>(
                LayoutInflater.from(parent.context), R.layout.item_my_chat_red_flower_msg,parent,false)
        }else{
            DataBindingUtil.inflate<ItemFriendChatRedFlowerMsgBinding>(
                LayoutInflater.from(parent.context), R.layout.item_friend_chat_red_flower_msg,parent,false)
        }
    }
    init {
        dataBindingView
    }

    override fun getRootView()=dataBindingView.root
}