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
import kotlinx.android.synthetic.main.dialog_mate_selection_buy_car.*

class BuyCarDialog(context: Context, val result:((BuyCarEnum)->Unit)?=null):Dialog(context) {
    private var currentChoice:BuyCarEnum?=null
    private val viewMappingBuyCarEnum by lazy {
        listOf(
            unlimited to BuyCarEnum.unlimited,
            option1 to BuyCarEnum.specialtyDown
        )
    }

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_mate_selection_buy_car)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewMappingBuyCarEnum.forEach { pair ->
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
    }

    override fun dismiss() {
        super.dismiss()
        result?.invoke(currentChoice?:return)
    }

    private fun choiceView(view:View){
        viewMappingBuyCarEnum.find {
            it.first==view
        }?.also {
            currentChoice=it.second
            updateView()
        }
    }

    private fun updateView(){
        viewMappingBuyCarEnum.forEach {
            it.first.isSelected=currentChoice==it.second
        }
    }
}