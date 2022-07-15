package com.xyzz.myutils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Context.createDialog(resId:Int):Dialog{
    val dialog=Dialog(this)
    dialog.also {
        it.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        it.window?.decorView?.setPadding(0,0,0,0)
        it.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        it.setContentView(resId)
    }
    return dialog
}

fun Activity.requestPermission(permission: Array<String>, message: String, requestCode: Int = 0):Boolean{
    if (Build.VERSION.SDK_INT< Build.VERSION_CODES.M){
        return true
    }
    val isGranted=permission.map { ContextCompat.checkSelfPermission(this, it) }.all { it== PackageManager.PERMISSION_GRANTED }
    if (isGranted){
        return true
    }else{
        AlertDialog.Builder(this)
                .setTitle("请求权限")
                .setMessage(message)
                .setPositiveButton("去申请"){ dialog, which->
                    requestPermissions(permission, requestCode)
                }
                .setNegativeButton("取消"){ dialog, which->

                }
                .show()
        return false
    }
}

fun Fragment.requestPermission(permission: Array<String>, message: String, requestCode: Int = 0):Boolean{
    if (Build.VERSION.SDK_INT< Build.VERSION_CODES.M){
        return true
    }
    val isGranted=permission.map { ContextCompat.checkSelfPermission(requireContext(), it) }.all { it== PackageManager.PERMISSION_GRANTED }
    if (isGranted){
        return true
    }else{
        AlertDialog.Builder(requireContext())
                .setTitle("请求权限")
                .setMessage(message)
                .setPositiveButton("去申请"){ dialog, which->
                    requestPermissions(permission, requestCode)
                }
                .setNegativeButton("取消"){ dialog, which->

                }
                .show()
        return false
    }
}