package com.twx.marryfriend.push

import android.content.Context
import com.twx.marryfriend.push.im.ImVivoMsgReceiver
import com.twx.marryfriend.push.mfr.MyVivoReceiver
import com.vivo.push.model.UPSNotificationMessage
import com.vivo.push.sdk.OpenClientPushMessageReceiver

class VivoMsgReceiver : OpenClientPushMessageReceiver() {
    private val receivers by lazy {
        arrayOf(ImVivoMsgReceiver(), MyVivoReceiver())
    }

    override fun onNotificationMessageClicked(context: Context, message: UPSNotificationMessage) {
        super.onNotificationMessageClicked(context, message)
        receivers.forEach {
            it.onNotificationMessageClicked(context,message)
        }
    }
}