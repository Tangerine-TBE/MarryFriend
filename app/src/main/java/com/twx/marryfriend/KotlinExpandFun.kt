package com.twx.marryfriend

import android.util.DisplayMetrics
import android.util.TypedValue

fun DisplayMetrics.dp2Px(dp:Float):Float{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,this)
}
