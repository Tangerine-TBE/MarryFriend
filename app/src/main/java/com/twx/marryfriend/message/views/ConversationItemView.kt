package com.twx.marryfriend.message.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.BaseDataBindingView
import com.twx.marryfriend.databinding.ItemListSessionMessageBinding
import com.twx.marryfriend.message.ConversationsItemModel

class ConversationItemView constructor(parent: ViewGroup):
    BaseDataBindingView<ConversationsItemModel> {
    private val dataBindingView=DataBindingUtil.inflate<ItemListSessionMessageBinding>(
        LayoutInflater.from(parent.context), R.layout.item_list_session_message,parent,false)

    override fun getRootView(): View {
        return dataBindingView.root
    }

    override fun setData(data: ConversationsItemModel?) {
        dataBindingView.conversationsItemModel=data
    }

}