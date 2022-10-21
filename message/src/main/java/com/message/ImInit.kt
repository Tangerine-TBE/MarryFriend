package com.message

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.lifecycle.MutableLiveData
import com.hyphenate.easeim.ImDemoInit

object ImInit {
    lateinit var application: Application
    fun init(application: Application){
        this.application=application
        val processAppName = application.getProcessName(Process.myPid())
        if (processAppName == null ||processAppName!=application.packageName) {
            return
        }
        ImDemoInit.initIm(application)
    }

    private fun Context.getProcessName(pID: Int): String? {
        var processName: String? = null
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l: List<*> = am.runningAppProcesses
        val i = l.iterator()
        while (i.hasNext()) {
            val info= i.next() as ActivityManager.RunningAppProcessInfo
            try {
                if (info.pid == pID) {
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