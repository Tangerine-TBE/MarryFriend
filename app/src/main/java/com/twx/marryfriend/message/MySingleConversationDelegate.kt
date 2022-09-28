package com.twx.marryfriend.message

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.adapter.EaseAdapterDelegate
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo
import com.hyphenate.easeui.utils.EaseCommonUtils
import com.hyphenate.easeui.utils.EaseDateUtils
import com.hyphenate.easeui.utils.EaseSmileUtils
import com.message.ImUserInfoService
import com.message.chat.CustomEvent
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.getUserExt
import kotlinx.android.synthetic.main.item_conversation_delegate.view.*
import java.util.*

class MySingleConversationDelegate: EaseAdapterDelegate<EaseConversationInfo, MySingleConversationDelegate.ViewHolder>() {
    override fun isForViewType(item: EaseConversationInfo?, position: Int): Boolean {
        return item != null && item.info is EMConversation
    }

    override fun onCreateViewHolder(parent: ViewGroup?, tag: String?): ViewHolder {
        val itemView=LayoutInflater.from(parent?.context).inflate(R.layout.item_conversation_delegate,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int, bean: EaseConversationInfo?) {
        super.onBindViewHolder(holder, position, bean)
        val item = bean?.info as? EMConversation?:return
        holder?:return
        if (item.conversationId()==ImConversationFragment.MY_HELPER_ID){
            holder.apply {
                messageHead.setImageResource(R.mipmap.ic_launcher)
                messageUserNickname.text="小秘书"
                conversationLastMsg.text="我是你的小秘书"

                isMessageRealName.visibility=View.GONE
                msgLock.visibility=View.GONE
                vipIdentification.visibility=View.GONE
                vipIdentification2.visibility=View.GONE
                messageCount.visibility=View.GONE
                messageMutualLikeIcon.visibility=View.GONE
            }
            return
        }

        if (bean.isTop) {
            holder.itemView.setBackgroundResource(com.hyphenate.easeui.R.drawable.ease_conversation_top_bg)
        } else {
            holder.itemView.background = ColorDrawable(Color.WHITE)
        }
        val username = item.conversationId()
        val defaultAvatar=UserInfo.getReversedDefHeadImage()

        holder.messageHead.setImageResource(defaultAvatar)
        holder.messageUserNickname.text = username

        EaseIM.getInstance().conversationInfoProvider?.getDefaultTypeAvatar(item.type.name)?.also {
            //设置头像
            Glide.with(holder.mContext)
                .load(it)
                .error(defaultAvatar)
                .into(holder.messageHead)
        }

        // add judgement for conversation type
        if (item.type == EMConversation.EMConversationType.Chat) {
            val userProvider = EaseIM.getInstance().userProvider
            val user = userProvider.getUser(username)
            val ext=user.getUserExt()
            if(UserInfo.isVip()||ext?.isSuperVip==true||ext?.isMutualLike==true){
                holder.msgLock.visibility=View.GONE

                holder.lastMsgView.visibility=View.VISIBLE
                holder.notVipShowView.visibility=View.GONE
            }else{
                holder.msgLock.visibility=View.VISIBLE

                holder.lastMsgView.visibility=View.GONE
                holder.notVipShowView.visibility=View.VISIBLE
            }

            if (!TextUtils.isEmpty(user.nickname)) {
                holder.messageUserNickname.text = user.nickname
            }
            if (!TextUtils.isEmpty(user.avatar)) {
                val drawable = holder.messageHead.drawable
                Glide.with(holder.mContext)
                    .load(user.avatar)
                    .error(drawable)
                    .into(holder.messageHead)
            }
            (ext?:ImUserInfoService.Ext()).also {
                if (it?.isMutualLike!=true&&it?.isFlower!=true){
                    holder.messageMutualLikeIcon.visibility=View.GONE
                }else{
                    holder.messageMutualLikeIcon.visibility=View.VISIBLE
                    if (it.isMutualLike){
                        holder.messageMutualLikeIcon.isSelected=true
                    }else if (it.isFlower){
                        holder.messageMutualLikeIcon.isSelected=false
                    }
                }
                if (it.isRealName==true){
                    holder.isMessageRealName.visibility=View.VISIBLE
                }else{
                    holder.isMessageRealName.visibility=View.GONE
                }
                if (it.isSuperVip){
                    holder.vipIdentification2.visibility=View.VISIBLE
                    holder.superVipHead.visibility=View.VISIBLE

                    holder.vipIdentification.visibility=View.GONE
                }else{
                    if (it.isVip){
                        holder.vipIdentification.visibility=View.VISIBLE
                    }else{
                        holder.vipIdentification.visibility=View.GONE
                    }
                    holder.vipIdentification2.visibility=View.GONE
                    holder.superVipHead.visibility=View.GONE
                }
                holder.apply {
                    conversationCity.text=it.city
                    conversationAge.text=it.age.toString()+"岁"
                    conversationOccupation.text=it.occupation
                    conversationEducation.text=it.education
                }
            }
        }

        showUnreadNum(holder, item.unreadMsgCount)

        if (item.allMsgCount != 0) {
            val lastMessage = item.lastMessage
            val text=if (lastMessage.type== EMMessage.Type.CUSTOM){
                val body=lastMessage.body as EMCustomMessageBody
                CustomEvent.getTip(body.event()).let {
                    if (it==null){
                        EaseCommonUtils.getMessageDigest(lastMessage, holder.mContext)
                    }else{
                        holder.msg_state.visibility = View.GONE
                        it
                    }
                }
            }else{
                val t=EaseCommonUtils.getMessageDigest(lastMessage, holder.mContext)
                if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                    holder.msg_state.setVisibility(View.VISIBLE)
                } else {
                    holder.msg_state.visibility = View.GONE
                }
                t
            }
            holder.conversationLastMsg.text = EaseSmileUtils.getSmiledText(holder.mContext, text)
            holder.messageTimeText.text = EaseDateUtils.getTimestampString(holder.mContext,Date(lastMessage.msgTime))
        }
    }

    private fun showUnreadNum(holder:ViewHolder, unreadMsgCount: Int) {
        if (unreadMsgCount > 0) {
            holder.messageCount.setText(handleBigNum(unreadMsgCount))
        } else {
            holder.messageCount.visibility = View.GONE
        }
    }

    private fun handleBigNum(unreadMsgCount: Int): String {
        return if (unreadMsgCount <= 99) {
            unreadMsgCount.toString()
        } else {
            "99+"
        }
    }

    class ViewHolder(itemView:View): EaseBaseRecyclerViewAdapter.ViewHolder<EaseConversationInfo>(itemView){
        val mContext by lazy {
            itemView.context
        }
        val messageUserNickname by lazy {
            with(itemView){
                this.messageUserNickname
            }
        }
        val messageHead by lazy {
            with(itemView){
                this.messageHead
            }
        }
        val conversationLastMsg by lazy {
            with(itemView){
                this.conversationLastMsg
            }
        }
        val messageTimeText by lazy {
            with(itemView){
                this.messageTimeText
            }
        }
        val messageCount by lazy {
            with(itemView){
                this.messageCount
            }
        }
        val msg_state by lazy {
            with(itemView){
                this.msg_state
            }
        }
        val messageMutualLikeIcon by lazy {
            with(itemView){
                this.messageMutualLikeIcon
            }
        }
        val isMessageRealName by lazy {
            with(itemView){
                this.isMessageRealName
            }
        }
        val vipIdentification by lazy {
            with(itemView){
                this.vipIdentification
            }
        }
        val vipIdentification2 by lazy {
            with(itemView){
                this.vipIdentification2
            }
        }
        val superVipHead by lazy {
            with(itemView){
                this.superVipHead
            }
        }
        val notVipShowView by lazy {
            with(itemView){
                this.notVipShowView
            }
        }
        val lastMsgView by lazy {
            with(itemView){
                this.lastMsgView
            }
        }
        val conversationCity by lazy {
            with(itemView){
                this.conversationCity
            }
        }
        val conversationAge by lazy {
            with(itemView){
                this.conversationAge
            }
        }
        val conversationOccupation by lazy {
            with(itemView){
                this.conversationOccupation
            }
        }
        val conversationEducation by lazy {
            with(itemView){
                this.conversationEducation
            }
        }
        val msgLock by lazy {
            with(itemView){
                this.msgLock
            }
        }
        override fun initView(itemView: View?) {

        }

        override fun setData(item: EaseConversationInfo, position: Int) {
            item.setOnSelectListener { isSelected ->
                if (isSelected) {
                    itemView.setBackgroundResource(com.hyphenate.easeui.R.drawable.ease_conversation_item_selected)
                } else {
                    if (item.isTop) {
                        itemView.setBackgroundResource(com.hyphenate.easeui.R.drawable.ease_conversation_top_bg)
                    } else {
                        itemView.background = ColorDrawable(Color.WHITE)
                    }
                }
            }
        }
    }
}