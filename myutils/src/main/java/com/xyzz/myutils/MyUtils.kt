package com.xyzz.myutils

import android.app.Application
import android.os.Handler
import android.os.Looper

object MyUtils {
    lateinit var application:Application
        private set
    fun init(application: Application){
        this.application=application
        Handler(Looper.getMainLooper()).post {
            ThreadExceptionLog.printUncaughtExceptionLog()
        }
    }
}