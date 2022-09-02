package com.twx.marryfriend.message.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.ItemListSessionMessageBinding
import com.twx.marryfriend.message.model.ConversationsItemModel

class TipConversationItemView  @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0):
    FrameLayout(context,attributeSet,defSty) {
    private val dataBindingView by lazy {
        DataBindingUtil.inflate<ItemListSessionMessageBinding>(
            LayoutInflater.from(context), R.layout.item_list_session_message_tip,this,false)
    }
    init {
        layoutParams= LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        addView(dataBindingView.root)
    }

    fun setData(data: ConversationsItemModel?) {
        dataBindingView.conversationsItemModel=data
    }
}