package com.twx.marryfriend

import android.app.Application
import com.kingja.loadsir.core.LoadSir
import com.message.ImInit
import com.message.ImUserManager
import com.xyzz.myutils.MyUtils


class AppApplication:Application() {
    companion object{
        lateinit var application :Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        application=this
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