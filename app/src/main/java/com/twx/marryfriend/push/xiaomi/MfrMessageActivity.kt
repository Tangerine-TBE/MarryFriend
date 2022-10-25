package com.twx.marryfriend.push.xiaomi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.twx.marryfriend.R
import com.umeng.message.UmengNotifyClick
import com.umeng.message.UmengNotifyClickActivity
import com.umeng.message.entity.UMessage

open class MfrMessageActivity : UmengNotifyClickActivity() {

    public override fun onMessage(msg: UMessage) {
        val body = msg.raw.toString()


        Log.i("guo", "mmmmmmmsg :$msg")

    }


}