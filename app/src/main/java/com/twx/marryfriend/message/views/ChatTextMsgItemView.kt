package com.twx.marryfriend.message.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.message.chat.TxtMessage
import com.twx.marryfriend.BR
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.friend.FriendInfoActivity
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
        initListener()
    }

    private fun initListener(){
        findViewById<View>(R.id.userHead).setOnClickListener {
            context.startActivity(FriendInfoActivity.getIntent(
                context,
                chatItemModel?.data?.from?.toIntOrNull()?:return@setOnClickListener
            ))
        }

    }

    private fun getView():ViewDataBinding{
        return if (chatItemModel?.isISend!=true){
            friendSendView
        }else{
            iSendView.also { binding ->
                val isUnread=chatItemModel?.data?.emMessage?.isUnread
                binding.root.findViewById<TextView>(R.id.msgReadState)?.also {
                    it.isSelected=(isUnread==true)
                    if (isUnread==true){
                        it.text="未读"
                    }else{
                        it.text="已读"
                    }
                }
            }
        }.also {
            Glide
                .with(this)
                .load(chatItemModel?.imageHead)
                .placeholder(UserInfo.getDefHeadImage())
                .error(UserInfo.getDefHeadImage())
                .into(it.root.findViewById(R.id.userHead))
        }
    }
}