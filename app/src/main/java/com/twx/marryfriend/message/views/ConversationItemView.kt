package com.twx.marryfriend.message.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.BaseDataBindingView
import com.twx.marryfriend.databinding.ItemListSessionMessageBinding
import com.twx.marryfriend.message.ChatActivity
import com.twx.marryfriend.message.model.ConversationsItemModel
import com.xyzz.myutils.show.toast

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
        val data=dataBindingView.conversationsItemModel
        this.setOnClickListener {
            if (data==null){
                toast(context,"数据为空")
                return@setOnClickListener
            }
            context?.startActivity(ChatActivity.getIntent(context, data.userId,data.nickname,data.userImage))
        }
    }

    override fun getRootView(): View {
        return dataBindingView.root
    }
}