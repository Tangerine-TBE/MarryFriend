package com.twx.marryfriend.recommend.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class MyNestedScrollView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0):
    NestedScrollView(context,attributeSet,defSty) {
    var actionUp:(()->Unit)?=null

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action==MotionEvent.ACTION_UP){
            actionUp?.invoke()
        }
        return super.onTouchEvent(ev)
    }
}