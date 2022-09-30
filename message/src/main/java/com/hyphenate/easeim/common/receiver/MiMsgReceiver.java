package com.hyphenate.easeim.common.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.Observer;

import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.model.ChatInfoBean;
import com.hyphenate.easeim.common.utils.PushUtils;
import com.hyphenate.easeim.common.utils.ToastUtils;
import com.hyphenate.push.platform.mi.EMMiMsgReceiver;
import com.hyphenate.util.EMLog;

import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xyzz.myutils.SPUtil;

import org.json.JSONObject;


/**
 * 获取有关小米音视频推送消息
 */
public class MiMsgReceiver extends EMMiMsgReceiver {

    static private String TAG = "MiMsgReceiver";

    public void onNotificationMessageClicked(Context context, MiPushMessage message) {

        String msgInfo = message.getContent();

        String userId = msgInfo.substring(msgInfo.indexOf("\"t\":") + 4, msgInfo.lastIndexOf(",\"f\""));
        String targetId = msgInfo.substring(msgInfo.indexOf("\"f\":") + 4, msgInfo.lastIndexOf(",\"m\":"));

        Log.i("guo", "LiveDataBus 发送信息");

        LiveDataBus.get().with("huanXin_push", ChatInfoBean.class).postValue(new ChatInfoBean(userId, targetId));

        EMLog.i(TAG, "onNotificationMessageClicked is called. " + message.toString());
        String extStr = message.getContent();
        EMLog.i(TAG, "onReceivePassThroughMessage get extras: " + extStr);
        try {
            JSONObject extras = new JSONObject(extStr);
            EMLog.i(TAG, "onReceivePassThroughMessage get extras: " + extras.toString());
            JSONObject object = extras.getJSONObject("e");
            if (object != null) {

                PushUtils.isRtcCall = object.getBoolean("isRtcCall");
                PushUtils.type = object.getInt("callType");
                EMLog.i(TAG, "onReceivePassThroughMessage get type: " + PushUtils.type);

            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        super.onNotificationMessageClicked(context, message);
    }
}
