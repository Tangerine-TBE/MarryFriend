package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.twx.marryfriend.R
import com.twx.marryfriend.enumeration.HousingEnum
import com.twx.marryfriend.enumeration.WantChildrenEnum
import kotlinx.android.synthetic.main.dialog_mate_selection_want_children.*

class WantChildrenDialog(context: Context, val result:((List<WantChildrenEnum>)->Unit)?=null):Dialog(context) {
    private val eduChoice by lazy {
        ArrayList<WantChildrenEnum>().apply {
            this.add(WantChildrenEnum.unlimited)
        }
    }
    private val viewMappingWantChildrenEnum by lazy {
        listOf(
            unlimited to WantChildrenEnum.unlimited,
            option1 to WantChildrenEnum.option1,
            option2 to WantChildrenEnum.option2,
            option3 to WantChildrenEnum.option3,
            option4 to WantChildrenEnum.option4
        )
    }

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_mate_selection_want_children)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewMappingWantChildrenEnum.forEach { pair ->
            pair.first.setOnClickListener {
                choiceView(it)
            }
        }
        confirm.setOnClickListener {
            result?.invoke(eduChoice.toList())
            dismiss()
        }
        dialog_close.setOnClickListener {
            confirm.performClick()
        }
        updateView()
        unlimited.isSelected=true
    }

    private fun choiceView(view:View){
        viewMappingWantChildrenEnum.find {
            it.first==view
        }?.also {
            WantChildrenEnum.changeChoice(it.second,eduChoice,!view.isSelected)
            updateView()
        }
    }

    private fun updateView(){
        viewMappingWantChildrenEnum.forEach {
            it.first.isSelected=eduChoice.contains(it.second)
        }
    }
}