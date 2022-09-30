package com.hyphenate.easeim.common.receiver;

import android.content.Context;
import android.util.Log;

import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.model.ChatInfoBean;
import com.hyphenate.easeim.common.utils.PushUtils;
import com.hyphenate.push.platform.mi.EMMiMsgReceiver;
import com.hyphenate.push.platform.vivo.EMVivoMsgReceiver;
import com.hyphenate.util.EMLog;
import com.vivo.push.model.UPSNotificationMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;

import org.json.JSONObject;


/**
 * 获取有关VIVO音视频推送消息
 */
public class VivoMsgReceiver extends EMVivoMsgReceiver {

    static private String TAG = "VivoMsgReceiver";

    public void onNotificationMessageClicked(Context context, UPSNotificationMessage message) {

        Log.i("guo","vivo Info" +  message.getContent());

//        String msgInfo = message.getContent();
//
//        String userId = msgInfo.substring(msgInfo.indexOf("\"t\":") + 4, msgInfo.lastIndexOf(",\"f\""));
//        String targetId = msgInfo.substring(msgInfo.indexOf("\"f\":") + 4, msgInfo.lastIndexOf(",\"m\":"));
//
//        Log.i("guo", "LiveDataBus 发送信息");
//
//        LiveDataBus.get().with("huanXin_push", ChatInfoBean.class).postValue(new ChatInfoBean(userId, targetId));

        super.onNotificationMessageClicked(context, message);
    }
}
