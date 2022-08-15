package com.xyzz.myutils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.WindowManager


fun Context.createDialog(resId:Int): Dialog {
    val dialog= Dialog(this)
    dialog.also {
        it.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        it.window?.decorView?.setPadding(0,0,0,0)
        it.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        it.setContentView(resId)
    }
    return dialog
}