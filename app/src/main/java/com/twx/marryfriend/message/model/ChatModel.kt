package com.twx.marryfriend.message.model

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField

class ChatModel :BaseObservable(){
    var title="对话"
    var editText = ObservableField<String>()
    var isEmojiShow=false
        set(value) {
            field=value
            notifyChange()
        }
    var isEditTextEmpty=true
        set(value) {
            field=value
            notifyChange()
        }
    var isMoreMsgShow=false
        set(value) {
            field=value
            notifyChange()
        }
}