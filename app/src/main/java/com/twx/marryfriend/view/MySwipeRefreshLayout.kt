package com.twx.marryfriend.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs

/**
 * @author: Administrator
 * @date: 2022/1/7
 */
class MySwipeRefreshLayout : SwipeRefreshLayout {
    constructor(context: Context, attes: AttributeSet) : super(context, attes) {}
    constructor(context: Context) : super(context)

    private var startX = 0
    private var beginScrolll = false //是否开始滑动
    private var startY: Int = 0
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x.toInt()
                startY = ev.y.toInt()
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x.toInt()
                val endY = ev.y.toInt()
                val disX = abs(endX - startX)
                val disY: Int = abs(endY - startY)
                if (disX > disY) {
                    if (!beginScrolll)
                        parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    beginScrolll = true
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                beginScrolll = false
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
