package com.twx.marryfriend

import android.app.Application
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.kingja.loadsir.core.LoadSir
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
        Glide.init(this, GlideBuilder().setSourceExecutor(
            GlideExecutor.newSourceBuilder().setThreadCount(
                GlideExecutor.calculateBestThreadCount()*5).build()))
    }
}