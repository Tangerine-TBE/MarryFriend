package com.twx.marryfriend.push.mfr;

import android.content.Context;
import android.content.Intent;

import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;

import org.android.agoo.mezu.MeizuPushReceiver;

/**
 * 魅族推送兼容
 */
public class MyMzPushMessageReceiver extends MzPushMessageReceiver {
    private final MeizuPushReceiver upush = new MeizuPushReceiver();

    @Override
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {
        upush.onRegisterStatus(context, registerStatus);
    }

    @Override
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {
        upush.onUnRegisterStatus(context, unRegisterStatus);
    }

    @Override
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {
        upush.onPushStatus(context, pushSwitchStatus);
    }

    @Override
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {
        upush.onSubTagsStatus(context, subTagsStatus);
    }

    @Override
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {
        upush.onSubAliasStatus(context, subAliasStatus);
    }

    @Override
    public void onMessage(Context context, Intent intent) {
        upush.onMessage(context, intent);
    }

    @Override
    public void onMessage(Context context, String s, String s1) {
        upush.onMessage(context, s, s1);
    }
}
