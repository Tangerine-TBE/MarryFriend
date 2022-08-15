package com.twx.marryfriend.message.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.message.chat.ImageMessage
import com.message.chat.TxtMessage
import com.twx.marryfriend.BR
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.*
import com.twx.marryfriend.message.model.ChatItemModel

class ChatImageMsgItemView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0):
    FrameLayout(context,attributeSet,defSty){
    init {
        layoutParams= LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
    }
    private val iSendView by lazy {
        DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(this.context), R.layout.item_my_chat_image_msg,this,false)
    }
    private val friendSendView by lazy {
        DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(this.context), R.layout.item_friend_chat_image_msg,this,false)
    }
    var chatItemModel: ChatItemModel<ImageMessage>?=null


    fun setData(chatItemModel: ChatItemModel<ImageMessage>){
        this.chatItemModel=chatItemModel
        getView().setVariable(BR.chatItemModel,chatItemModel)
        removeAllViews()
        addView(getView().root)
    }

    private fun getView(): ViewDataBinding {
        return if (chatItemModel?.isISend!=true){
            friendSendView
        }else{
            iSendView
        }
    }
}