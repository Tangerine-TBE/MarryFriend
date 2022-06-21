package com.twx.module_base.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Handler

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


//        UMConfigure.init(
//            applicationContext,
//            UMConfigure.DEVICE_TYPE_PHONE,
//            "621dca382b8de26e11d274d8"
//        )
//        UMConfigure.setLogEnabled(true)




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