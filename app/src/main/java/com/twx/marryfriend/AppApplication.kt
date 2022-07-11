package com.twx.marryfriend

import android.app.Application
import com.xyzz.myutils.MyUtils
import com.xyzz.myutils.ThreadExceptionLog


class AppApplication:Application() {
    companion object{
        lateinit var application :Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        application=this
        MyUtils.init(this)

//        LoadSir.beginBuilder()
//            .addCallback(ErrorCallback()) //添加各种状态页
//            .addCallback(EmptyCallback())
//            .addCallback(LoadingCallback())
//            .addCallback(TimeoutCallback())
//            .addCallback(CustomCallback())
//            .setDefaultCallback(LoadingCallback::class.java) //设置默认状态页
//            .commit()
    }
}