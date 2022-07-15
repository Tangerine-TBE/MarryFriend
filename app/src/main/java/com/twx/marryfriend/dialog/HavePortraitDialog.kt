package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.twx.marryfriend.R
import com.twx.marryfriend.enumeration.BuyCarEnum
import com.twx.marryfriend.enumeration.HeadPortraitEnum
import kotlinx.android.synthetic.main.dialog_mate_selection_buy_car.*
import kotlinx.android.synthetic.main.dialog_mate_selection_have_portrait.*
import kotlinx.android.synthetic.main.dialog_mate_selection_have_portrait.close
import kotlinx.android.synthetic.main.dialog_mate_selection_have_portrait.option1
import kotlinx.android.synthetic.main.dialog_mate_selection_have_portrait.unlimited

class HavePortraitDialog(context: Context, val result:((HeadPortraitEnum)->Unit)?=null):Dialog(context) {
    private var currentChoice: HeadPortraitEnum?=null
    private val viewMappingHeadPortraitEnum by lazy {
        listOf(
            unlimited to HeadPortraitEnum.unlimited,
            option1 to HeadPortraitEnum.specialtyDown
        )
    }

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_mate_selection_have_portrait)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewMappingHeadPortraitEnum.forEach { pair ->
            pair.first.text=pair.second.title
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
        viewMappingHeadPortraitEnum.find {
            it.first==view
        }?.also {
            currentChoice=it.second
            updateView()
        }
    }

    private fun updateView(){
        viewMappingHeadPortraitEnum.forEach {
            it.first.isSelected=currentChoice==it.second
        }
    }
}