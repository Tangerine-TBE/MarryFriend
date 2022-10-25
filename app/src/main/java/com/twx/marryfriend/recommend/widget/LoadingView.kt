package com.twx.marryfriend.recommend.widget

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import kotlinx.android.synthetic.main.item_recommend_loading_animation.view.*

class LoadingView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0): FrameLayout(context,attributeSet,defSty) {
    private val valueAnimator by lazy {
        ValueAnimator.ofFloat(0f,4f)
    }
    init {
        inflate(context,R.layout.item_recommend_loading_animation,this)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility==View.VISIBLE){
            startAnimation()
        }else{
            valueAnimator?.cancel()
        }
    }

    fun startAnimation(){
        valueAnimator?.cancel()
        val scx=6f
        this.apply {
            Glide.with(userHeadImage)
                .load(UserInfo.getHeadPortrait())
                .placeholder(UserInfo.getUserSex().smallHead)
                .error(UserInfo.getUserSex().smallHead)
                .into(userHeadImage)
            valueAnimator?.removeAllUpdateListeners()
            valueAnimator?.addUpdateListener {
                view1.scaleX=view1.scaleX.let {sx->
                    (sx+0.03f)%scx
                }
                view1.scaleY=view1.scaleX.let {sx->
                    (sx+0.03f)%scx
                }

                view2.scaleX=view1.scaleX.let {sx->
                    (sx+0.03f+0.5f)%scx
                }
                view2.scaleY=view1.scaleX.let {sx->
                    (sx+0.03f+0.5f)%scx
                }

                view3.scaleX=view1.scaleX.let {sx->
                    (sx+0.03f+1f)%scx
                }
                view3.scaleY=view1.scaleX.let {sx->
                    (sx+0.03f+1f)%scx
                }

                view4.scaleX=view1.scaleX.let {sx->
                    (sx+0.03f+2f)%scx
                }
                view4.scaleY=view1.scaleX.let {sx->
                    (sx+0.03f+2f)%scx
                }
            }
            valueAnimator?.duration=2500
            valueAnimator?.interpolator=LinearInterpolator()
            valueAnimator?.repeatMode=ValueAnimator.RESTART
            valueAnimator?.repeatCount=ValueAnimator.INFINITE
            valueAnimator?.start()
        }
    }

    fun isRunAnimation():Boolean{
        return valueAnimator.isRunning
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator?.cancel()
    }
}