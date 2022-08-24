package com.twx.marryfriend.message.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.message.ImMessageManager
import com.message.chat.SendFlowerMessage
import com.message.chat.VoiceMessage
import com.twx.marryfriend.BR
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.model.ChatItemModel
import com.twx.marryfriend.recommend.PlayAudio

class ChatVoiceItemView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0):
    FrameLayout(context,attributeSet,defSty) {
    init {
        layoutParams= LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
    }
    private val iSendView by lazy {
        DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(this.context), R.layout.item_my_chat_voice_msg,this,false)
    }
    private val friendSendView by lazy {
        DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(this.context), R.layout.item_friend_chat_voice_msg,this,false)
    }
    var chatItemModel: ChatItemModel<VoiceMessage>?=null

    fun setData(chatItemModel: ChatItemModel<VoiceMessage>){
        this.chatItemModel=chatItemModel
        getView().setVariable(BR.chatItemModel,chatItemModel)
        removeAllViews()
        addView(getView().root)
        initListener()
    }


    private fun initListener(){
        findViewById<View>(R.id.userHead).setOnClickListener {
            context.startActivity(FriendInfoActivity.getIntent(context,chatItemModel?.data?.from?.toIntOrNull()?:return@setOnClickListener))
        }
        findViewById<View>(R.id.playVoice).setOnClickListener {
            val uri=chatItemModel?.data?.uri
            if (uri!=null){
                ImMessageManager.sendReadAck(chatItemModel?.data?.emMessage?:return@setOnClickListener)
                PlayAudio.play(uri,{
                    //开始播放
                },{
                    //播放完成
                })
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        PlayAudio.stop()
    }

    private fun getView(): ViewDataBinding {
        return if (chatItemModel?.isISend!=true){
            friendSendView
        }else{
            iSendView
        }.also {
            Glide.with(this)
                .load(chatItemModel?.imageHead)
                .placeholder(UserInfo.getDefHeadImage())
                .error(UserInfo.getDefHeadImage())
                .into(it.root.findViewById(R.id.userHead))
        }
    }
}