package com.twx.marryfriend.message.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.databinding.ItemListSessionMessageBinding
import com.twx.marryfriend.message.model.ConversationsItemModel
import kotlinx.android.synthetic.main.item_list_session_message_tip.view.*

class TipConversationItemView  @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0):
    FrameLayout(context,attributeSet,defSty) {
    private val dataBindingView by lazy {
        DataBindingUtil.inflate<ItemListSessionMessageBinding>(
            LayoutInflater.from(context), R.layout.item_list_session_message_tip,this,false)
    }
    init {
        layoutParams= LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        addView(dataBindingView.root)
        setData(null)
    }

    fun setData(data: ConversationsItemModel?) {
        dataBindingView.conversationsItemModel=data
        Glide.with(this).load(data?.userImage).placeholder(UserInfo.getReversedDefHeadImage()).error(UserInfo.getReversedDefHeadImage()).into(messageHead)
    }
}