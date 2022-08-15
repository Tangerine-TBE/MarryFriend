package com.twx.marryfriend.push.xiaomi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import com.twx.marryfriend.R
import com.umeng.message.UmengNotifyClick
import com.umeng.message.entity.UMessage

open class MfrMessageActivity : Activity() {
    private val mNotificationClick: UmengNotifyClick = object : UmengNotifyClick() {
        public override fun onMessage(msg: UMessage) {
            val body = msg.raw.toString()

            Log.d(MfrMessageActivity.Companion.TAG, "body: $body")
            if (!TextUtils.isEmpty(body)) {
                runOnUiThread(Runnable { (findViewById<TextView>(R.id.tv)).text = body })
            }
        }
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        Log.i("guo", "onCREATE")

        Log.i("guo","huaweihuawei")

        setContentView(R.layout.activity_mfr_message)
        mNotificationClick.onCreate(this, intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mNotificationClick.onNewIntent(intent)
    }

    companion object {
        private const val TAG = "MfrMessageActivity"
    }

}