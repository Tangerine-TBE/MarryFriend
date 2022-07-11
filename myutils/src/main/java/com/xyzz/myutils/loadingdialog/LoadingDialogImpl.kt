package com.xyzz.myutils.loadingdialog

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.view.WindowManager
import android.widget.TextView
import com.xyzz.myutils.R
import com.xyzz.myutils.createDialog
import java.lang.IllegalStateException

class LoadingDialogImpl: ILoadingDialog {
    private var mProgressDialog: Dialog?=null

    override fun create(context: Context): ILoadingDialog {
        mProgressDialog=context.createDialog(R.layout.dialog_loading_myutils)
        return this
    }

    override fun setMessage(text: String): ILoadingDialog {
        mProgressDialog?.findViewById<TextView>(R.id.dialogMsg)?.text=text
        return this
    }

    override fun setCancelable(cancelable: Boolean): ILoadingDialog {
        mProgressDialog?.setCancelable(cancelable)
        return this
    }

    override fun show() {
        mProgressDialog?:throw IllegalStateException("请先调用 create(Context) 方法创建对话框")
        mProgressDialog?.show()
    }

    override fun dismiss() {
        mProgressDialog?.dismiss()
    }
}