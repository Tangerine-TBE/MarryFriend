package com.twx.marryfriend.recommend.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageView

class RecommendImage @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0):AppCompatImageView(context,attributeSet,defSty) {

    private val clipPath by lazy { Path() }
    private val filletRect by lazy {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20f*2,resources.displayMetrics)
    }
    private val paint by lazy {
        Paint().apply {
            this.strokeWidth=resources.displayMetrics.density*2
            this.isAntiAlias=true
            this.style=Paint.Style.STROKE
            this.color=Color.parseColor("#FFFFFFFF")
        }
    }
    private var isDrawFrame=false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        clipPath.addArc(RectF(0f,0f,filletRect,filletRect),-180f,90f)
        clipPath.lineTo(filletRect/2,0f)
        clipPath.lineTo(w-filletRect/2,0f)
        clipPath.addArc(RectF(w -filletRect,0f,w.toFloat(),filletRect),-90f,90f)
        clipPath.lineTo(w.toFloat(),filletRect/2)
        clipPath.lineTo(w.toFloat(),h.toFloat())

        clipPath.lineTo(0f,h.toFloat())
        clipPath.lineTo(0f,filletRect/2)
//        clipPath.close()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?:return
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
        if (isDrawFrame) {
            canvas.drawPath(clipPath, paint)
        }
    }
}