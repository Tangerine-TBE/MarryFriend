package com.twx.marryfriend.message.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.message.chat.ImageMessage
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.*
import com.twx.marryfriend.message.model.ChatItemModel

class ChatImageMsgItemView constructor(parent: ViewGroup):BaseDataBindingView {
    private val dataBindingView by lazy {
        if (true){
            DataBindingUtil.inflate<ItemMyChatImageMsgBinding>(
                LayoutInflater.from(parent.context), R.layout.item_my_chat_image_msg,parent,false)
        }else{
            DataBindingUtil.inflate<ItemFriendChatImageMsgBinding>(
                LayoutInflater.from(parent.context), R.layout.item_friend_chat_image_msg,parent,false)
        }
    }
    init {
        dataBindingView
    }
    private var chatItemModel: ChatItemModel<ImageMessage>?=null

    fun setData(chatItemModel: ChatItemModel<ImageMessage>){
        this.chatItemModel=chatItemModel
    }

    override fun getRootView()=dataBindingView.root
}