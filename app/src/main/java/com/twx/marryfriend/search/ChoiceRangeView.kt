package com.twx.marryfriend.search

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.FloatRange
import com.twx.marryfriend.R
import com.twx.marryfriend.dp2Px
import kotlin.math.min

@SuppressLint("ClickableViewAccessibility")
class ChoiceRangeView @JvmOverloads constructor(context: Context, attrs:AttributeSet?=null, defStyle:Int=0): View(context,attrs,defStyle){
    private val backgroundPaint by lazy {
        Paint().apply {
            this.color=Color.parseColor("#FFF2F2F2")
            this.style=Paint.Style.FILL
        }
    }
    private val progressPaint by lazy {
        Paint().apply {
            this.shader = LinearGradient(0f,0f,width.toFloat(),0f,
                Color.parseColor("#FFFF4444"),Color.parseColor("#FFFF40CC"),Shader.TileMode.REPEAT)
            this.style=Paint.Style.FILL
        }
    }
    private val startBitmap by lazy {
        BitmapFactory.decodeResource(resources,R.mipmap.ic_choice_range_start)
    }
    private val endBitmap by lazy {
        BitmapFactory.decodeResource(resources,R.mipmap.ic_choice_range_end)
    }
    private var startPoint=0f
        set(value) {
            field=if(value<0f){
                0f
            }else if (value>width-startBitmap.width){
                width-startBitmap.width+0f
            }else{
                value
            }
            if (endPoint>startPoint){
                rangeCall?.invoke(startPoint/(width-endBitmap.width),endPoint/(width-endBitmap.width))
            }else{
                rangeCall?.invoke(endPoint/(width-endBitmap.width),startPoint/(width-endBitmap.width))
            }
        }
    private var endPoint=0f
        set(value) {
            field=if(value<0f){
                0f
            }else if (value>width-endBitmap.width){
                width-endBitmap.width+0f
            }else{
                value
            }
            if (endPoint>startPoint){
                rangeCall?.invoke(startPoint/(width-endBitmap.width),endPoint/(width-endBitmap.width))
            }else{
                rangeCall?.invoke(endPoint/(width-endBitmap.width),startPoint/(width-endBitmap.width))
            }
        }
    private var bitmapHeight=0f
    private val bitmapPaint by lazy {
        Paint()
    }
    var rangeCall:((Float,Float)->Unit)?=null
    var interceptUse:(()->Boolean)?=null

    fun reset(){
        startPoint=0f
        endPoint=width - bitmapHeight
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode=MeasureSpec.getMode(heightMeasureSpec)
        val size=MeasureSpec.getSize(heightMeasureSpec)
        if (mode==MeasureSpec.AT_MOST){
            setMeasuredDimension(
                getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
                min(size,startBitmap.height)
            )
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        startPoint=0f
        bitmapHeight=min(endBitmap.width,h).toFloat()
        endPoint=w - bitmapHeight
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        canvas.drawRect(0f,height/2f-resources.displayMetrics.dp2Px(2f),width.toFloat(),height/2f+resources.displayMetrics.dp2Px(2f),backgroundPaint)
        canvas.drawRect(startPoint+bitmapHeight/2f,
            height/2f-resources.displayMetrics.dp2Px(4f),
            endPoint+bitmapHeight/2f,
            height/2f+resources.displayMetrics.dp2Px(4f),progressPaint)

        canvas.drawBitmap(startBitmap,startPoint,height/2f-startBitmap.height/2f,bitmapPaint)
        canvas.drawBitmap(endBitmap,endPoint,height/2f-endBitmap.height/2f,bitmapPaint)
    }

    private var isHandle=false
    private var isHandleStart=false
    private var preX=0f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?:return super.onTouchEvent(event)
        if (interceptUse?.invoke()==true){
            return super.onTouchEvent(event)
        }

        when(event.action){
            MotionEvent.ACTION_DOWN->{
                parent?.requestDisallowInterceptTouchEvent(true)
                val x=event.x
                val startPointRange=startPoint-10 .. startPoint+startBitmap.width+10
                val endPointRange=endPoint-10 .. endPoint+endBitmap.width+10
                if (x in startPointRange){
                    parent?.requestDisallowInterceptTouchEvent(true)
                    isHandle=true
                    isHandleStart=true
                }else if (x in endPointRange){
                    parent?.requestDisallowInterceptTouchEvent(true)
                    isHandle=true
                    isHandleStart=false
                }else{
                    isHandle=false
                }
                preX=x
            }
            MotionEvent.ACTION_MOVE->{
                if (isHandle){
                    val dx=event.x-preX
                    if (isHandleStart){
                        startPoint+=dx
                    }else{
                        endPoint+=dx
                    }
                    preX=event.x
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP->{

            }
        }
        return true
    }
}