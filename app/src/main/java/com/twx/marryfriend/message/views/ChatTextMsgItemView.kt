package com.twx.marryfriend.message.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.message.chat.TxtMessage
import com.twx.marryfriend.BR
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.ItemFriendChatTextMsgBinding
import com.twx.marryfriend.databinding.ItemMyChatTextMsgBinding
import com.twx.marryfriend.message.model.ChatItemModel

class ChatTextMsgItemView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet?=null,defSty:Int=0):FrameLayout(context,attributeSet,defSty) {
    init {
        layoutParams= LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
    }

    private val iSendView by lazy {
        DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(this.context), R.layout.item_my_chat_text_msg,this,false)
    }
    private val friendSendView by lazy {
        DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(this.context), R.layout.item_friend_chat_text_msg,this,false)
    }

    var chatItemModel: ChatItemModel<TxtMessage>?=null

    fun setData(data:ChatItemModel<TxtMessage>?){
        chatItemModel=data
        removeAllViews()
        addView(getView().root)
        getView().setVariable(BR.chatItemModel,data)
    }

    private fun getView():ViewDataBinding{
        return if (chatItemModel?.isISend!=true){
            friendSendView
        }else{
            iSendView
        }
    }
}