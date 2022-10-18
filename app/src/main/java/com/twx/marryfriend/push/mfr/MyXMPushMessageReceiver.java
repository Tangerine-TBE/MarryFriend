package com.twx.marryfriend.push.mfr;

import android.content.Context;

import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.android.agoo.xiaomi.MiPushBroadcastReceiver;

/**
 * 小米推送兼容
 */
public class MyXMPushMessageReceiver extends PushMessageReceiver {
    private final MiPushBroadcastReceiver upush = new MiPushBroadcastReceiver();

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage miPushCommandMessage) {
        upush.onReceiveRegisterResult(context, miPushCommandMessage);
    }

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
        upush.onReceivePassThroughMessage(context, miPushMessage);
    }
}
