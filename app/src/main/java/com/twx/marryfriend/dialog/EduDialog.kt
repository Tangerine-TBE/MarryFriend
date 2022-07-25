package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.twx.marryfriend.R
import com.twx.marryfriend.enumeration.EduEnum
import com.twx.marryfriend.enumeration.MarriageEnum
import kotlinx.android.synthetic.main.dialog_mate_selection_edu.*

class EduDialog(context: Context,val result:((List<EduEnum>)->Unit)?=null):Dialog(context) {
    private val eduChoice by lazy {
        ArrayList<EduEnum>().apply {
            this.add(EduEnum.unlimited)
        }
    }
    private val viewMappingEduEnum by lazy {
        listOf(
            unlimited to EduEnum.unlimited,
            option1 to EduEnum.specialtyDown,
            option2 to EduEnum.specialty,
            option3 to EduEnum.undergraduate,
            option4 to EduEnum.master,
            option5 to EduEnum.doctor,
        )
    }

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_mate_selection_edu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewMappingEduEnum.forEach { pair ->
            pair.first.setOnClickListener {
                choiceView(it)
            }
        }
        confirm.setOnClickListener {
            result?.invoke(eduChoice.toList())
            dismiss()
        }
        dialog_close.setOnClickListener {
            result?.invoke(eduChoice.toList())
            dismiss()
        }
        updateView()
        unlimited.isSelected=true
    }

    private fun choiceView(view:View){
        viewMappingEduEnum.find {
            it.first==view
        }?.also { pair ->
            EduEnum.changeChoice(pair.second,eduChoice,!view.isSelected)
            updateView()
        }
    }

    private fun updateView(){
        viewMappingEduEnum.forEach {
            it.first.isSelected=eduChoice.contains(it.second)
        }
    }
}