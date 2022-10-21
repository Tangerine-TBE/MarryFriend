package com.twx.marryfriend.push.im

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.hyphenate.push.platform.mi.EMMiMsgReceiver
import com.twx.marryfriend.message.ImChatActivity
import com.twx.marryfriend.push.PushManager
import com.xiaomi.mipush.sdk.MiPushCommandMessage
import com.xiaomi.mipush.sdk.MiPushMessage
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.wLog
import org.json.JSONObject


class ImMiMsgReceiver : EMMiMsgReceiver() {
    override fun onReceivePassThroughMessage(context: Context?, message: MiPushMessage?) {
        iLog("11","收到小米推送")
        super.onReceivePassThroughMessage(context, message)
    }

    override fun onNotificationMessageArrived(context: Context?, message: MiPushMessage?) {
        iLog("22","收到小米推送")
        super.onNotificationMessageArrived(context, message)
    }

    override fun onReceiveMessage(p0: Context?, p1: MiPushMessage?) {
        iLog("33","收到小米推送")
        super.onReceiveMessage(p0, p1)
    }

    override fun onReceiveRegisterResult(context: Context?, message: MiPushCommandMessage?) {
        iLog("44","收到小米推送")
        super.onReceiveRegisterResult(context, message)
    }

    override fun onCommandResult(context: Context?, message: MiPushCommandMessage?) {
        iLog("55","收到小米推送")
        super.onCommandResult(context, message)
    }

    override fun onRequirePermissions(p0: Context?, p1: Array<out String>?) {
        iLog("66","收到小米推送")
        super.onRequirePermissions(p0, p1)
    }

    override fun onNotificationMessageClicked(context: Context, miPushMessage: MiPushMessage) {
        val extStr = miPushMessage.content
        iLog(extStr,"收到小米推送点击")
        try {
            val extras = JSONObject(extStr)
            val t = extras.getString("t")
            val f = extras.getString("f")
            PushManager.onNotificationMessageClicked(context, t, f)
        }catch (e:Exception){
            PushManager.onNotificationMessageClicked(context)
            wLog(e.stackTraceToString())
        }
    }
}