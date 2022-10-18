package com.twx.marryfriend.push.mfr;

import android.content.Context;

import com.vivo.push.sdk.OpenClientPushMessageReceiver;

import org.android.agoo.vivo.PushMessageReceiverImpl;

/**
 * vivo推送兼容
 */
public class MyVivoReceiver extends OpenClientPushMessageReceiver {

    private final PushMessageReceiverImpl upush = new PushMessageReceiverImpl();

    @Override
    public void onReceiveRegId(Context context, String regId) {
        upush.onReceiveRegId(context, regId);
    }
}
