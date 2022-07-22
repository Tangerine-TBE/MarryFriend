package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.twx.marryfriend.R
import com.twx.marryfriend.enumeration.OnLineEnum
import kotlinx.android.synthetic.main.dialog_mate_selection_on_line.*

class OnLineDialog(context: Context, val result:((OnLineEnum)->Unit)?=null):Dialog(context) {
    private var currentChoice:OnLineEnum?=null
    private val viewMappingOnLineEnum by lazy {
        listOf(
            unlimited to OnLineEnum.unlimited,
            option1 to OnLineEnum.option1
        )
    }

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_mate_selection_on_line)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewMappingOnLineEnum.forEach { pair ->
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
        viewMappingOnLineEnum.find {
            it.first==view
        }?.also {
            currentChoice=it.second
            updateView()
        }
    }

    private fun updateView(){
        viewMappingOnLineEnum.forEach {
            it.first.isSelected=currentChoice==it.second
        }
    }
}