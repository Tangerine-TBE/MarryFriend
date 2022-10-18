package com.twx.marryfriend.push.mfr;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.taobao.accs.utl.ALog;
import com.taobao.accs.utl.UtilityImpl;
import com.taobao.agoo.BaseNotifyClickActivity;

import org.android.agoo.control.NotifManager;
import org.android.agoo.oppo.OppoMsgParseImpl;

/**
 * OPPO推送兼容
 */
public class MyOppoPushCompat {

    public static final String TAG = "OppoPush";
    private static final String OPPO_TOKEN = "OPPO_TOKEN";

    public static void register(Context context, String appKey, String appSecret) {
        try {
            Context cxt = context.getApplicationContext();
            if (!UtilityImpl.isMainProcess(cxt)) {
                ALog.i(TAG, "not in main process, return");
                return;
            }
            HeytapPushManager.init(cxt, (cxt.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
            if (HeytapPushManager.isSupportPush(context)) {
                BaseNotifyClickActivity.addNotifyListener(new OppoMsgParseImpl());
                ALog.i(TAG, "register oppo begin ");
                HeytapPushManager.register(cxt, appKey, appSecret, new ICallBackResultService() {
                    @Override
                    public void onRegister(int i, String s) {
                        ALog.i(TAG, "onRegister regid=" + s);
                        reportToken(cxt, s);
                    }

                    @Override
                    public void onUnRegister(int i) {
                        ALog.e(TAG, "onUnRegister code=" + i);
                    }

                    @Override
                    public void onSetPushTime(int i, String s) {
                        ALog.i(TAG, "onSetPushTime");
                    }

                    @Override
                    public void onGetPushStatus(int i, int i1) {
                        ALog.i(TAG, "onGetPushStatus");
                    }

                    @Override
                    public void onGetNotificationStatus(int i, int i1) {
                        ALog.i(TAG, "onGetNotificationStatus");
                    }

                    @Override
                    public void onError(int i, String s) {
                        ALog.i(TAG, "onError");
                    }
                });
            } else {
                ALog.i(TAG, "not support oppo push");
            }
        } catch (Throwable t) {
            ALog.e(TAG, "register error", t);
        }
    }

    private static void reportToken(Context context, String token) {
        if (!TextUtils.isEmpty(token) && context != null) {
            NotifManager notifyManager = new NotifManager();
            notifyManager.init(context.getApplicationContext());
            notifyManager.reportThirdPushToken(token, OPPO_TOKEN, true);
        }
    }
}
