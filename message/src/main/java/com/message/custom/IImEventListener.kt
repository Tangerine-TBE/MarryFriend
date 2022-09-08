package com.message.custom

import android.view.View
import com.hyphenate.chat.EMMessage
import com.message.chat.CustomMessage

interface IImEventListener {
    fun click(view: View,event: CustomMessage.CustomEvent,emMessage: EMMessage)
}