package com.xyzz.myutils.loadingdialog

import android.content.Context

interface ILoadingDialog {

    fun create(context: Context): ILoadingDialog

    fun setMessage(text:String): ILoadingDialog

    fun setCancelable(cancelable:Boolean): ILoadingDialog

    fun show()

    fun dismiss()
}