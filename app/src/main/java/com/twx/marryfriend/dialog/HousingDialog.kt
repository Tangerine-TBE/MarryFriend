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
import kotlinx.android.synthetic.main.dialog_mate_selection_housing.*

class HousingDialog(context: Context, val result:((List<HousingEnum>)->Unit)?=null):Dialog(context) {
    private val eduChoice by lazy {
        HashSet<HousingEnum>()
    }
    private val viewMappingHousingEnum by lazy {
        listOf(
            withFamily to HousingEnum.withFamily,
            purchasedHouse to HousingEnum.purchasedHouse,
            rentHouse to HousingEnum.rentHouse,
            marryBuyHouse to HousingEnum.marryBuyHouse,
            dormitory to HousingEnum.dormitory,
        )
    }

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_mate_selection_housing)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewMappingHousingEnum.forEach { pair ->
            pair.first.setOnClickListener {
                choiceView(it)
            }
        }
        confirm.setOnClickListener {
            result?.invoke(eduChoice.toList())
            dismiss()
        }
        housingSkip.setOnClickListener {
            dismiss()
        }
        updateView()
    }

    private fun choiceView(view:View){
        viewMappingHousingEnum.find {
            it.first==view
        }?.also {
            if (view.isSelected){//移除一个筛选
                eduChoice.remove(it.second)
            }else{//增加一个筛选
                eduChoice.add(it.second)
            }
            updateView()
        }
    }

    private fun updateView(){
        viewMappingHousingEnum.forEach {
            it.first.isSelected=eduChoice.contains(it.second)
        }
    }
}