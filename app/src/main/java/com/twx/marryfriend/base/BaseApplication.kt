package com.twx.marryfriend.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Handler
import com.twx.marryfriend.push.help.PushHelper
import com.umeng.commonsdk.UMConfigure

open class BaseApplication : Application() {

    private var onForegroundListener: OnForegroundListener? = null

    open fun getInstance(): BaseApplication? {
        if (baseApplication == null) {
            baseApplication = BaseApplication()
        }
        return baseApplication
    }

    companion object {
        lateinit var application: Application
        var appContext: Context? = null
        var baseApplication: BaseApplication? = null
        fun getContext(): Context {
            return appContext!!
        }

        var mMainHandler: Handler? = null
        fun getMainHandler(): Handler {
            return mMainHandler!!
        }

        lateinit var packName: String

    }


    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()
        application = this
        appContext = baseContext
        mMainHandler = Handler()
        packName = packageName


        /**
         * 初始化友盟SDK
         */

        //日志开关
        UMConfigure.setLogEnabled(true)

        //预初始化
//        PushHelper.preInit(this)
        UMConfigure.preInit(this, "62e74fde1f47e265d4e8aa28", "_360")


        initChild()


    }


    open fun initChild() {
    }


    interface OnForegroundListener {
        fun listener();
    }

    fun setOnForegroundListener(listener: OnForegroundListener) {
        this.onForegroundListener = listener
    }

}

