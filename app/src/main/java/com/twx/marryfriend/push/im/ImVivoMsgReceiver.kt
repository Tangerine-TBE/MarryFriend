package com.twx.marryfriend.push.im

import android.content.Context
import com.hyphenate.push.platform.vivo.EMVivoMsgReceiver
import com.twx.marryfriend.push.PushManager
import com.vivo.push.model.UPSNotificationMessage
import com.xyzz.myutils.show.iLog

class ImVivoMsgReceiver : EMVivoMsgReceiver() {
    override fun onNotificationMessageClicked(context: Context, message: UPSNotificationMessage) {
        super.onNotificationMessageClicked(context, message)
        val map: Map<String, String> = message.params
        iLog("${map}","收到vivo推送点击")
        PushManager.onNotificationMessageClicked(context,message)
    }
}