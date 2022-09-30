package com.twx.marryfriend.base

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.message.custom.MyHelperAdapterDelegate
import com.twx.marryfriend.AppApplication
import com.twx.marryfriend.UserInfo

object BaseConstant {
    val application by lazy { AppApplication.application }
    val channel by lazy {
        try {
            val applicationInfo: ApplicationInfo = application.packageManager.getApplicationInfo(
                application.packageName,
                PackageManager.GET_META_DATA
            )
            applicationInfo.metaData?.getString("CHANNEL_VALUE")?:"_channel"
        } catch (e:Exception) {
            e.printStackTrace()
            "_channel"
        }
    }
}