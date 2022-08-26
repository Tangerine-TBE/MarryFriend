package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.twx.marryfriend.R
import kotlinx.android.synthetic.main.dialog_friends_setting.*

class FollowReportDialog(context: Context):Dialog(context) {

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_friends_setting)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        closeDialog.setOnClickListener {
            dismiss()
        }
    }

    fun setFollowText(text:String){
        follow.text=text
    }

    fun setFollowListener(action:()->Unit){
        follow.setOnClickListener {
            action.invoke()
            dismiss()
        }
    }

    fun setReportListener(action:()->Unit){
        report.setOnClickListener {
            action.invoke()
            dismiss()
        }
    }
}