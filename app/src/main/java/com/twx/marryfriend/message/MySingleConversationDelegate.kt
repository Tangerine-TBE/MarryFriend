package com.twx.marryfriend.message

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.adapter.EaseAdapterDelegate
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.domain.EaseUser
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo
import com.hyphenate.easeui.utils.EaseCommonUtils
import com.hyphenate.easeui.utils.EaseDateUtils
import com.hyphenate.easeui.utils.EaseSmileUtils
import com.message.ImUserInfoService
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
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

        val username = item.conversationId()
        val defaultAvatar=UserInfo.getReversedDefHelloHeadImage()

        holder.name?.text=bean.timestamp.toString()
        holder.avatar.setImageResource(defaultAvatar);
        holder.name.text = username

        if(UserInfo.isVip()){
           holder.msgLock.visibility=View.GONE
        }else{
            holder.msgLock.visibility=View.VISIBLE
        }

        val infoProvider = EaseIM.getInstance().conversationInfoProvider
        if (infoProvider != null) {
            val avatarResource = infoProvider.getDefaultTypeAvatar(item.type.name)
            if (avatarResource != null) {
                //设置头像
                Glide.with(holder.mContext)
                    .load(avatarResource)
                    .error(defaultAvatar)
                    .into(holder.avatar)
            }
        }
        // add judgement for conversation type
        if (item.type == EMConversation.EMConversationType.Chat) {
            val userProvider = EaseIM.getInstance().userProvider
            if (userProvider != null) {
                val user = userProvider.getUser(username)
                if (user != null) {
                    if (!TextUtils.isEmpty(user.nickname)) {
                        holder.name.text = user.nickname
                    }
                    if (!TextUtils.isEmpty(user.avatar)) {
                        val drawable = holder.avatar.drawable
                        Glide.with(holder.mContext)
                            .load(user.avatar)
                            .error(drawable)
                            .into(holder.avatar)
                    }

                    user.getUserExt().also {
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
                        if (UserInfo.isVip()){
                            holder.notVipShowView.visibility=View.GONE
                        }else{
                            holder.notVipShowView.visibility=View.VISIBLE
                            holder.apply {
                                conversationCity.text=it.city
                                conversationAge.text=it.age.toString()+"岁"
                                conversationOccupation.text=it.occupation
                                conversationEducation.text=it.education
                            }
                        }
                    }
                }
            }
        }

        showUnreadNum(holder, item.unreadMsgCount)

        if (item.allMsgCount != 0) {
            val lastMessage = item.lastMessage
            holder.message.text = EaseSmileUtils.getSmiledText(holder.mContext, EaseCommonUtils.getMessageDigest(lastMessage, holder.mContext))
            holder.time.text = EaseDateUtils.getTimestampString(holder.mContext,Date(lastMessage.msgTime))
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.mMsgState.setVisibility(View.VISIBLE)
            } else {
                holder.mMsgState.setVisibility(View.GONE)
            }
        }
    }

    private fun showUnreadNum(holder:ViewHolder, unreadMsgCount: Int) {
        if (unreadMsgCount > 0) {
            holder.mUnreadMsgNumber.setText(handleBigNum(unreadMsgCount))
        } else {
            holder.mUnreadMsgNumber.visibility = View.GONE
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
        val name by lazy {
            with(itemView){
                this.messageUserNickname
            }
        }
        val avatar by lazy {
            with(itemView){
                this.messageHead
            }
        }
        val message by lazy {
            with(itemView){
                this.conversationLastMsg
            }
        }
        val time by lazy {
            with(itemView){
                this.messageTimeText
            }
        }
        val mUnreadMsgNumber by lazy {
            with(itemView){
                this.messageCount
            }
        }
        val mMsgState by lazy {
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

    private val gson by lazy {
        Gson()
    }
    fun EaseUser.getUserExt():ImUserInfoService.Ext{
        if (this.ext==null){
            return ImUserInfoService.Ext()
        }else{
            return gson.fromJson(this.ext,ImUserInfoService.Ext::class.java)?:ImUserInfoService.Ext()
        }
    }
}