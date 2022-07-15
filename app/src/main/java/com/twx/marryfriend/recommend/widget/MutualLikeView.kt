package com.twx.marryfriend.recommend.widget

import android.content.Context
import android.util.AttributeSet
import android.view.animation.*
import android.widget.FrameLayout
import com.twx.marryfriend.R
import kotlinx.android.synthetic.main.item_recommend_mutual_like.view.*

class MutualLikeView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet?=null,defSty:Int=0):FrameLayout(context,attributeSet,defSty) {
    init {
        inflate(context, R.layout.item_recommend_mutual_like,this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mutualLikeView1.post {
            ScaleAnimation(1f,5f,1f,5f,
                mutualLikeView1.width/2f,mutualLikeView1.height/2f).also {
                it.duration=5000
                it.repeatCount=Animation.INFINITE
                it.repeatMode=Animation.RESTART
                it.interpolator=LinearInterpolator()
                mutualLikeView1.startAnimation(it)
            }
            ScaleAnimation(1f,10f,1f,10f,mutualLikeView1.width/2f,mutualLikeView1.height/2f).also {
                it.duration=5000
                it.repeatCount=Animation.INFINITE
                it.repeatMode=Animation.RESTART
                it.interpolator=LinearInterpolator()
                mutualLikeView2.startAnimation(it)
            }
            ScaleAnimation(1f,15f,1f,15f,mutualLikeView1.width/2f,mutualLikeView1.height/2f).also {
                it.duration=5000
                it.repeatCount=Animation.INFINITE
                it.repeatMode=Animation.RESTART
                it.interpolator=LinearInterpolator()
                mutualLikeView3.startAnimation(it)
            }


            AnimationSet(true).also {
                it.addAnimation(TranslateAnimation(-width/2f,0f,0f,0f))
                it.addAnimation(ScaleAnimation(0f,1f,0f,1f,taHeadView.width/2f,taHeadView.height/2f))
                it.duration=1000
                it.interpolator=LinearInterpolator()
                myHeadView.startAnimation(it)
            }
            AnimationSet(true).also {
                it.addAnimation(TranslateAnimation(width/2f,0f,0f,0f))
                it.addAnimation(ScaleAnimation(0f,1f,0f,1f,taHeadView.width/2f,taHeadView.height/2f))
                it.duration=1000
                it.interpolator=LinearInterpolator()
                taHeadView.startAnimation(it)
            }
        }
    }
}