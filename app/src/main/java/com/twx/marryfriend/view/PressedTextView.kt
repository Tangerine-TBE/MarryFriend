package com.twx.marryfriend.view

import android.content.Context
import android.util.AttributeSet

class PressedTextView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0):
    androidx.appcompat.widget.AppCompatTextView(context,attributeSet,defSty){
    init {
        setOnClickListener {  }
    }
    var onPressedListener:((Boolean)->Unit)?=null

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        onPressedListener?.invoke(pressed)
    }
}