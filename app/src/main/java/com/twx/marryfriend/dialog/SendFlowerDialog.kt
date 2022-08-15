package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.WindowManager
import com.twx.marryfriend.R
import com.xyzz.myutils.SPUtil
import kotlinx.android.synthetic.main.dialog_send_flower.*

class SendFlowerDialog private constructor(context: Context,continueAction:()->Unit):Dialog(context) {
    companion object{
        private const val IS_AGREE_KEY="send_flower_not_tip_k"
        fun sendFlowerTip(context: Context,continueAction:()->Unit){
            if (SPUtil.instance.getBoolean(IS_AGREE_KEY)){
                continueAction.invoke()
            }else{
                SendFlowerDialog(context,continueAction).show()
            }
        }
    }
    init {
        this.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        this.window?.decorView?.setPadding(0,0,0,0)
        this.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        this.window?.setGravity(Gravity.BOTTOM)
        this.setContentView(R.layout.dialog_send_flower)
//        isAgree.isSelected = SPUtil.instance.getBoolean(IS_AGREE_KEY)
        isAgree.setOnClickListener {
            it.isSelected=!it.isSelected
            SPUtil.instance.putBoolean(IS_AGREE_KEY,it.isSelected)
        }
        sendFlowers.setOnClickListener {
            continueAction.invoke()
            dismiss()
        }
        closeDialog.setOnClickListener {
            dismiss()
        }
    }
}