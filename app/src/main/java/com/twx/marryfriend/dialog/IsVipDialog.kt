package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.twx.marryfriend.R
import com.twx.marryfriend.enumeration.IsVipEnum
import kotlinx.android.synthetic.main.dialog_mate_selection_is_vip.*

class IsVipDialog(context: Context, val result:((IsVipEnum)->Unit)?=null):Dialog(context) {
    private var currentChoice: IsVipEnum?=null
    private val viewMappingIsVipEnum by lazy {
        listOf(
            unlimited to IsVipEnum.unlimited,
            option1 to IsVipEnum.specialtyDown
        )
    }

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_mate_selection_is_vip)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewMappingIsVipEnum.forEach { pair ->
            pair.first.setOnClickListener {
                choiceView(it)
                dismiss()
            }
        }
        close.setOnClickListener {
            currentChoice=null
            dismiss()
        }
        updateView()
    }

    override fun dismiss() {
        super.dismiss()
        result?.invoke(currentChoice?:return)
    }

    private fun choiceView(view:View){
        viewMappingIsVipEnum.find {
            it.first==view
        }?.also {
            currentChoice=it.second
            updateView()
        }
    }

    private fun updateView(){
        viewMappingIsVipEnum.forEach {
            it.first.isSelected=currentChoice==it.second
        }
    }
}