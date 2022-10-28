package com.twx.marryfriend.push.mfr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.message.ImInit.init
import com.message.ImUserManager
import com.twx.marryfriend.R
import com.twx.marryfriend.base.BaseConstant
import com.twx.marryfriend.begin.BeginActivity
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.main.MainActivity.Companion.getIntent
import com.umeng.message.UmengNotifyClick
import com.umeng.message.entity.UMessage
import com.xyzz.myutils.show.wLog

class MfrMessageActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mfr_message)
        mNotificationClick.onCreate(this, intent)
    }

    private val mNotificationClick: UmengNotifyClick = object : UmengNotifyClick() {
        override fun onMessage(msg: UMessage) {

            Log.i("guo", "mNotificationClick")

            Log.i("guo", "msg.custom  : ${msg.custom}")

            SPStaticUtils.put(Constant.PUSH_ACTION, msg.custom)

            startActivity(Intent(this@MfrMessageActivity2, BeginActivity::class.java))


//            ThreadUtils.runOnUiThread {
//
//                init(BaseConstant.application)
//
//                ImUserManager.login("110", {
//
//                    Log.i("guo", "success")
//                    startActivity(getIntent(this@MfrMessageActivity2, true, msg.custom))
//
//                }, { code, message ->
//                    ToastUtils.showShort("数据登录失败，请重试")
//                    AppUtils.exitApp()
//                })
//            }

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mNotificationClick.onNewIntent(intent)
    }

}