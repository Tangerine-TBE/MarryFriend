package com.twx.marryfriend.message.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.BaseDataBindingView
import com.twx.marryfriend.databinding.ItemChatTextMsgBinding

class ChatTextMsgItemView constructor(parent: ViewGroup):BaseDataBindingView {
    private val dataBindingView by lazy {
        DataBindingUtil.inflate<ItemChatTextMsgBinding>(
            LayoutInflater.from(parent.context), R.layout.item_chat_text_msg,parent,false)
    }
    init {
        dataBindingView.root
    }

    override fun getRootView()=dataBindingView.root
}