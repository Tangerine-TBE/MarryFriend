package com.message.custom

import android.view.View
import android.view.ViewGroup
import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeim.R
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate
import com.hyphenate.easeui.interfaces.MessageListItemClickListener
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder
import com.hyphenate.easeui.widget.chatrow.EaseChatRow
import com.message.chat.CustomEvent

class SecurityTipAdapterDelegate: EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder>(){
    override fun isForViewType(item: EMMessage?, position: Int): Boolean {
//        item?.getBooleanAttribute()
        return item?.type == EMMessage.Type.CUSTOM &&
                (item.body as? EMCustomMessageBody)?.event()== CustomEvent.security.code
    }

    override fun getEaseChatRow(parent: ViewGroup?, isSender: Boolean): EaseChatRow {
        return object :EaseChatRow(parent?.context,isSender){
            override fun onInflateView() {
                inflater.inflate(R.layout.item_sys_chat_tip_msg, this)
            }

            override fun onFindViewById() {
//            findViewById<>()
            }

            override fun onSetUpView() {
                setOnClickListener {
                    ImCustomEventListenerManager.click(it, CustomEvent.security,message)
                }
            }
        }
    }

    override fun createViewHolder(
        view: View,
        itemClickListener: MessageListItemClickListener?
    ): EaseChatRowViewHolder {
        return EaseChatRowViewHolder(view,itemClickListener)
    }
}