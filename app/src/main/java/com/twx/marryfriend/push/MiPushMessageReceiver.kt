package com.twx.marryfriend.push

import android.content.Context
import com.twx.marryfriend.push.im.ImMiMsgReceiver
import com.twx.marryfriend.push.mfr.MyXMPushMessageReceiver
import com.xiaomi.mipush.sdk.MiPushCommandMessage
import com.xiaomi.mipush.sdk.MiPushMessage
import com.xiaomi.mipush.sdk.PushMessageReceiver

class MiPushMessageReceiver: PushMessageReceiver() {
    private val receivers by lazy {
        arrayOf(MyXMPushMessageReceiver(), ImMiMsgReceiver())
    }

    override fun onReceivePassThroughMessage(p0: Context?, p1: MiPushMessage?) {
        super.onReceivePassThroughMessage(p0, p1)
        receivers.forEach {
            it.onReceivePassThroughMessage(p0, p1)
        }
    }

    override fun onNotificationMessageClicked(p0: Context?, p1: MiPushMessage?) {
        super.onNotificationMessageClicked(p0, p1)
        receivers.forEach {
            it.onNotificationMessageClicked(p0, p1)
        }
    }

    override fun onNotificationMessageArrived(p0: Context?, p1: MiPushMessage?) {
        super.onNotificationMessageArrived(p0, p1)
        receivers.forEach {
            it.onNotificationMessageArrived(p0, p1)
        }
    }

    override fun onReceiveMessage(p0: Context?, p1: MiPushMessage?) {
        super.onReceiveMessage(p0, p1)
        receivers.forEach {
            it.onReceiveMessage(p0, p1)
        }
    }

    override fun onReceiveRegisterResult(p0: Context?, p1: MiPushCommandMessage?) {
        super.onReceiveRegisterResult(p0, p1)
        receivers.forEach {
            it.onReceiveRegisterResult(p0, p1)
        }
    }

    override fun onCommandResult(p0: Context?, p1: MiPushCommandMessage?) {
        super.onCommandResult(p0, p1)
        receivers.forEach {
            it.onCommandResult(p0, p1)
        }
    }

    override fun onRequirePermissions(p0: Context?, p1: Array<out String>?) {
        super.onRequirePermissions(p0, p1)
        receivers.forEach {
            it.onRequirePermissions(p0, p1)
        }
    }
}