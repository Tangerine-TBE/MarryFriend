package com.twx.marryfriend.message.views

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.BaseDataBindingView
import com.twx.marryfriend.databinding.ItemListSessionMessageBinding
import com.twx.marryfriend.message.ChatActivity
import com.twx.marryfriend.message.model.ConversationsItemModel

class ConversationItemView constructor(parent: ViewGroup): BaseDataBindingView {
    private val dataBindingView by lazy {
        DataBindingUtil.inflate<ItemListSessionMessageBinding>(
            LayoutInflater.from(parent.context), R.layout.item_list_session_message,parent,false)
    }
    init {
        dataBindingView.root.initListener()
    }

    fun setData(data: ConversationsItemModel?) {
        dataBindingView.conversationsItemModel=data
    }

    private fun View.initListener(){
        this.setOnClickListener {
            context?.startActivity(Intent(context,ChatActivity::class.java))
        }
    }

    override fun getRootView(): View {
        return dataBindingView.root
    }
}