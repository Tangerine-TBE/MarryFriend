package com.twx.marryfriend

import android.app.Application
import android.content.Intent
import com.kingja.loadsir.core.LoadSir
import com.message.ImInit
import com.message.ImUserManager
import com.twx.marryfriend.base.BaseApplication
import com.twx.marryfriend.begin.BeginActivity
import com.twx.marryfriend.utils.SpUtil
import com.xyzz.myutils.MyUtils


class AppApplication : BaseApplication() {
    companion object {
        lateinit var application: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        MyUtils.init(this)

        LoadSir.beginBuilder()
            .addCallback(DefEmptyDataCallBack()) //添加各种状态页
//            .addCallback(EmptyCallback())
//            .addCallback(LoadingCallback())
//            .addCallback(TimeoutCallback())
//            .addCallback(CustomCallback())
//            .setDefaultCallback(LoadingCallback::class.java) //设置默认状态页
            .commit()
       ImInit.init(this)
    }
}