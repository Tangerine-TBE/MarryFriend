package com.twx.marryfriend.recommend.widget

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.twx.marryfriend.R
import com.xyzz.myutils.toast
import kotlinx.android.synthetic.main.item_recommend_guide.view.*
import kotlin.math.abs

class RecommendGuideView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet?=null,defSty:Int=0):FrameLayout(context,attributeSet,defSty) {
    init {
        inflate(context,R.layout.item_recommend_guide,this)
    }
    private var isUpScroll=false
    private var isLeftScroll=false
    private var isRightScroll=false
    private var isClick=false
    private var isHandler=false

    private val gesturelistener by lazy {
        object : GestureDetector.SimpleOnGestureListener(){
            override fun onDown(e: MotionEvent?): Boolean {
                isHandler=false
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (isHandler){
                    return false
                }
                if (!isUpScroll){
                    if (abs(distanceY)> abs(distanceX)){
                        if (distanceY>0){
                            isUpScroll=true
                            testText.text="上划完成"
                            isHandler= true
                        }
                    }
                }
                if (!isLeftScroll&&isUpScroll){
                    if (abs(distanceY) < abs(distanceX)){
                        if (distanceX>0){
                            isLeftScroll=true
                            testText.text="左划完成"
                            isHandler= true
                        }
                    }
                }
                if (!isRightScroll&&isUpScroll&&isLeftScroll){
                    if (abs(distanceY) < abs(distanceX)){
                        if (distanceX<0){
                            isRightScroll=true
                            testText.text="右划完成"
                            isHandler= true
                        }
                    }
                }

                return true
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                if (!isClick&&isUpScroll&&isLeftScroll&&isRightScroll){
                    isClick=true
                    testText.text="送花完成"
                    visibility= View.GONE
                    return true
                }
                return super.onSingleTapUp(e)
            }
        }
    }
    private val gestureDetector=GestureDetector(context,gesturelistener)
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}