package com.xyzz.myutils.loadingdialog

object LoadingDialogManager {
    fun createLoadingDialog():ILoadingDialog{
        return LoadingDialogImpl()
    }
}