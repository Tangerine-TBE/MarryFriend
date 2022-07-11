package com.xyzz.myutils

object ThreadExceptionLog {
    fun printUncaughtExceptionLog(){
        val uncaughtExceptionHandler=Thread.currentThread().uncaughtExceptionHandler
        Thread.currentThread().setUncaughtExceptionHandler { t, e ->
            eLog(e.stackTraceToString(),"闪退了")
            uncaughtExceptionHandler?.uncaughtException(t,e)
        }
    }
}