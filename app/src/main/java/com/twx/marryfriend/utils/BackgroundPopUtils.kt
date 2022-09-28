package com.twx.marryfriend.utils

import android.content.Context
import android.net.Uri

/**
 * @author: Administrator
 * @date: 2022/9/28
 */
object BackgroundPopUtils {

    fun isVivoBgStartPermissionAllowed(context: Context): Boolean {
        return getVivoBgStartPermissionStatus(context) == 0
    }

    /**
     * 判断Vivo后台弹出界面状态， 1无权限，0有权限
     * @param context context
     */
    private fun getVivoBgStartPermissionStatus(context: Context): Int {
        val uri: Uri = Uri.parse("content://com.vivo.permissionmanager.provider.permission/start_bg_activity")
        val selection = "pkgname = ?"
        val selectionArgs = arrayOf(context.packageName)
        var state = 1
        try {
            context.contentResolver.query(uri, null, selection, selectionArgs, null)?.use {
                if (it.moveToFirst()) {
                    state = it.getInt(it.getColumnIndex("currentstate"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return state
    }


}