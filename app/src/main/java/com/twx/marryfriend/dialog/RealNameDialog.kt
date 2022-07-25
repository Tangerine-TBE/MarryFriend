package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.twx.marryfriend.R
import com.twx.marryfriend.enumeration.RealNameEnum
import kotlinx.android.synthetic.main.dialog_mate_selection_real_name.*

class RealNameDialog(context: Context, val result:((RealNameEnum)->Unit)?=null):Dialog(context) {
    private var currentChoice:RealNameEnum?=null
    private val viewMappingRealNameEnum by lazy {
        listOf(            
            unlimited to RealNameEnum.unlimited,
            option1 to RealNameEnum.option1
        )
    }

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_mate_selection_real_name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewMappingRealNameEnum.forEach { pair ->
            pair.first.text=pair.second.title
            pair.first.setOnClickListener {
                choiceView(it)
                dismiss()
            }
        }
        dialog_close.setOnClickListener {
            currentChoice=null
            dismiss()
        }
        updateView()
        unlimited.isSelected=true
    }
    override fun dismiss() {
        super.dismiss()
        result?.invoke(currentChoice?:return)
    }

    private fun choiceView(view:View){
        viewMappingRealNameEnum.find {
            it.first==view
        }?.also {
            currentChoice=it.second
            updateView()
        }
    }

    private fun updateView(){
        viewMappingRealNameEnum.forEach {
            it.first.isSelected=currentChoice==it.second
        }
    }
}