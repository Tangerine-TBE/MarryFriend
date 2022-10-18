package com.twx.marryfriend.push.mfr;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.twx.marryfriend.R;
import com.umeng.message.UmengNotifyClickActivity;
import com.umeng.message.entity.UMessage;

import java.util.Map;

/**
 * 厂商通道配置启动的Activity
 * 点击小米、vivo等厂商渠道的推送通知消息后跳转的activity
 * 推荐使用方式：MfrMessageActivity2
 *
 * 必须在AndroidManifest.xml中MfrMessageActivity标签下配置：
 *  1. 配置 android:exported="true"
 *  2. 新增 intent-filter
 *  <intent-filter>
 *     <action android:name="android.intent.action.VIEW" />
 *     <category android:name="android.intent.category.DEFAULT" />
 *     <category android:name="android.intent.category.BROWSABLE" />
 *     <data
 *         android:host="${applicationId}"
 *         android:path="/thirdpush"
 *         android:scheme="agoo" />
 *   </intent-filter>
 */
//@Deprecated
public class MfrMessageActivity extends UmengNotifyClickActivity {
    private static final String TAG = "MfrMessageActivity";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.mfr_message_layout);
    }

    @Override
    protected void onMessage(UMessage msg) {
        super.onMessage(msg);
        final String body = msg.getRaw().toString();
        if (!TextUtils.isEmpty(body)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textView = findViewById(R.id.tv);
                    if (textView != null) {
                        textView.setText(body);
                    }
                }
            });
        }
        Log.d(TAG, "msg:" + body);
        Map<String, String> extra = msg.getExtra();
        Log.d(TAG, "extra:" + extra);
    }

}
