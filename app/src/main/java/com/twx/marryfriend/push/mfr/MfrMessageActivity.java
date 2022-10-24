package com.twx.marryfriend.push.mfr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.twx.marryfriend.R;
import com.twx.marryfriend.main.MainActivity;
import com.umeng.message.UmengNotifyClick;
import com.umeng.message.entity.UMessage;


/**
 * 厂商通道配置启动的Activity
 * 点击小米、vivo等厂商渠道的推送通知消息后跳转的activity
 * <p>
 * 必须在AndroidManifest.xml中MfrMessageActivity2标签下配置：
 * 1. 配置 android:exported="true"
 * 2. 新增 intent-filter
 * <intent-filter>
 * <action android:name="android.intent.action.VIEW" />
 * <category android:name="android.intent.category.DEFAULT" />
 * <category android:name="android.intent.category.BROWSABLE" />
 * <data
 * android:host="${applicationId}"
 * android:path="/thirdpush"
 * android:scheme="agoo" />
 * </intent-filter>
 */
public class MfrMessageActivity extends Activity {
    private static final String TAG = "MfrMessageActivity";

    private final UmengNotifyClick mNotificationClick = new UmengNotifyClick() {
        @Override
        public void onMessage(UMessage msg) {

            startActivity(MainActivity.Companion.getIntent(MfrMessageActivity.this, true, msg.custom));

        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.mfr_message_layout);
        mNotificationClick.onCreate(this, getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mNotificationClick.onNewIntent(intent);
    }
}
