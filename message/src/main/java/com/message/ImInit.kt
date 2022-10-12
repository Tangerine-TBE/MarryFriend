package com.message

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.lifecycle.MutableLiveData
import com.hyphenate.easeim.ImDemoInit
import com.hyphenate.easeim.common.livedatas.LiveDataBus

object ImInit {
    lateinit var application: Application
    //环信BaseActivity有处理踢下线逻辑
    val imLoginState by lazy {
        MutableLiveData<String>()
    }
    fun init(application: Application){
        this.application=application
        val pid = Process.myPid()
        val processAppName = application.getAppName(pid)
        if (processAppName == null ||processAppName!=application.packageName) {
            return
        }
        ImDemoInit.initIm(application)
    }

    private fun Context.getAppName(pID: Int): String? {
        var processName: String? = null
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l: List<*> = am.runningAppProcesses
        val i = l.iterator()
        val pm = this.packageManager
        while (i.hasNext()) {
            val info: ActivityManager.RunningAppProcessInfo =
                i.next() as ActivityManager.RunningAppProcessInfo
            try {
                if (info.pid === pID) {
                    processName = info.processName
                    return processName
                }
            } catch (e: Exception) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName
    }
}