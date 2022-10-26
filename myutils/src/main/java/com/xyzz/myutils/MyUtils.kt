package com.xyzz.myutils

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.xyzz.myutils.show.ThreadExceptionLog

object MyUtils {
    lateinit var application:Application
        private set
    val authorities by lazy {
        application.packageName+".myUtils.file_provider"
    }
    fun init(application: Application){
        this.application=application
        application.registerActivityLifecycleCallbacks(LifecycleCallbacks.instance)
        Handler(Looper.getMainLooper()).post {
            ThreadExceptionLog.printUncaughtExceptionLog()
        }
    }

    fun getFrontBackstageLiveData()=LifecycleCallbacks.instance.frontBackstageLiveData

    fun getLastResumedActivityLiveData()=LifecycleCallbacks.instance.lastResumedActivity

    fun getAllRunActivity()=LifecycleCallbacks.instance.getRunActivity()
}