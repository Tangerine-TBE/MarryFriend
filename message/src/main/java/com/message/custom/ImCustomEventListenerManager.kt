package com.message.custom

import android.view.View
import com.hyphenate.chat.EMMessage
import com.message.chat.CustomEvent

object ImCustomEventListenerManager {
    private val listener by lazy {
        ArrayList<IImEventListener>()
    }

    fun click(view: View, event: CustomEvent, emMessage: EMMessage){
        listener.forEach {
            it.click(view, event,emMessage)
        }
    }

    fun addListener(imEventListener: IImEventListener){
        listener.clear()
        listener.add(imEventListener)
    }

    fun removeListener(imEventListener: IImEventListener){
        listener.remove(imEventListener)
    }
}