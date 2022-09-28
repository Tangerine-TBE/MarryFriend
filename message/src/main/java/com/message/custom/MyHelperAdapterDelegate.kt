package com.message.custom

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeim.R
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate
import com.hyphenate.easeui.interfaces.MessageListItemClickListener
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder
import com.hyphenate.easeui.widget.chatrow.EaseChatRow
import com.message.ImConstant
import com.message.chat.CustomMessage

class MyHelperAdapterDelegate: EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder>(){

    override fun isForViewType(item: EMMessage?, position: Int): Boolean {
        if (item?.type != EMMessage.Type.CUSTOM){
            return false
        }
        val body= item.body as? EMCustomMessageBody
        val event=body?.event()

        return event == ImConstant.FILL_IN_SAY_HELLO||
                event == ImConstant.UP_LOAD_HEAD||
                event == ImConstant.UP_LOAD_PICTURE||
                event == ImConstant.VIP_EXPIRE
    }

    override fun getEaseChatRow(parent: ViewGroup?, isSender: Boolean): EaseChatRow {
        return object :EaseChatRow(parent?.context,isSender){
            val helperTipIcon by lazy {
                findViewById<ImageView>(R.id.helperTipIcon)
            }
            val helperTipText by lazy {
                findViewById<TextView>(R.id.helperTipText)
            }
            val gotoHandel by lazy {
                findViewById<View>(R.id.gotoHandel)
            }

            val body by lazy {
                message.body as? EMCustomMessageBody
            }

            override fun onInflateView() {
                inflater.inflate(R.layout.custom_my_helper_msg, this)
            }

            override fun onFindViewById() {

            }

            override fun onSetUpView() {
                when(body?.event()){
                    ImConstant.FILL_IN_SAY_HELLO->{
                        helperTipIcon
                        helperTipText
                        gotoHandel
                    }
                    ImConstant.UP_LOAD_HEAD->{

                    }
                    ImConstant.UP_LOAD_PICTURE->{

                    }
                    ImConstant.VIP_EXPIRE->{

                    }
                    else->{

                    }
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