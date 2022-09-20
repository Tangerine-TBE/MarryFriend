package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import kotlinx.android.synthetic.main.dialog_upload_head.*

class UploadHeadDialog(context: Context): Dialog(context) {

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_upload_head)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Glide.with(context)
            .load(UserInfo.getUserSex().uploadHeadDef)
            .into(sexDefHead)
        closeDialog.setOnClickListener {
            dismiss()
        }
        takePicture.setOnClickListener {
            context.startActivity(IntentManager.getUpHeadImageIntent(context))
            dismiss()
        }
        selectPicture.setOnClickListener {
            context.startActivity(IntentManager.getUpHeadImageIntent(context))
            dismiss()
        }
    }
}