package com.twx.marryfriend.message.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.message.chat.TxtMessage
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.BaseDataBindingView
import com.twx.marryfriend.databinding.ItemFriendChatTextMsgBinding
import com.twx.marryfriend.databinding.ItemMyChatTextMsgBinding
import com.twx.marryfriend.message.model.ChatItemModel

class ChatTextMsgItemView constructor(parent: ViewGroup):BaseDataBindingView {
    private val dataBindingView by lazy {
        if (true){
            DataBindingUtil.inflate<ItemFriendChatTextMsgBinding>(
                LayoutInflater.from(parent.context), R.layout.item_friend_chat_text_msg,parent,false)
        }else{
            DataBindingUtil.inflate<ItemMyChatTextMsgBinding>(
                LayoutInflater.from(parent.context), R.layout.item_my_chat_text_msg,parent,false)
        }
    }
    init {
        dataBindingView
    }
    var chatItemModel: ChatItemModel<TxtMessage>?=null

    override fun getRootView()=dataBindingView.root
}