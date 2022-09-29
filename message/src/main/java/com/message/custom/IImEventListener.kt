package com.message.custom

import android.view.View
import com.hyphenate.chat.EMMessage
import com.message.chat.CustomEvent

interface IImEventListener {
    fun click(view: View, event: CustomEvent, emMessage: EMMessage)
}