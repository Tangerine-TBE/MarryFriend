package com.twx.marryfriend.push.mfr;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import org.android.agoo.huawei.HuaweiRcvService;

/**
 * 华为推送兼容
 */
public class MyHmsMessageService extends HmsMessageService {
    private final HuaweiRcvService upush = new HuaweiRcvService();

    @Override
    public void onNewToken(String s) {
        upush.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        upush.onMessageReceived(remoteMessage);
    }
}
