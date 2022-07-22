package com.twx.marryfriend.recommend.widget

import android.content.Context
import android.util.TypedValue

object CardConfig {
    //屏幕上最多同时显示几个Item
    var MAX_SHOW_COUNT = 0

    //每一级Scale相差0.05f，translationY相差7dp左右
    var SCALE_GAP = 0f
    var TRANS_Y_GAP = 0
    fun initConfig(context: Context?) {
        context?:return
        MAX_SHOW_COUNT = 3
        SCALE_GAP = 0.05f
        // 把非标准尺寸转换成标准尺寸
        TRANS_Y_GAP = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            25f,
            context.resources.displayMetrics
        ).toInt()
    }
}