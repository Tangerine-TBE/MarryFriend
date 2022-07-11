package com.twx.marryfriend.recommend.widget

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.LinearInterpolator
import com.kingja.loadsir.callback.Callback
import com.twx.marryfriend.R
import kotlinx.android.synthetic.main.item_recommend_loading_animation.view.*

class LoadingCallback: Callback() {
    private var valueAnimator:ValueAnimator?=null

    override fun onCreateView(): Int {
        valueAnimator=ValueAnimator.ofFloat(0f,4f)
        return R.layout.item_recommend_loading_animation
    }

    override fun onViewCreate(context: Context?, view: View?) {
        super.onViewCreate(context, view)
        val scx=5f
        view?.apply {
            valueAnimator?.addUpdateListener {
                view1.scaleX=view1.scaleX.let {sx->
                    (sx+0.02f)%scx
                }
                view1.scaleY=view1.scaleX.let {sx->
                    (sx+0.02f)%scx
                }

                view2.scaleX=view1.scaleX.let {sx->
                    (sx+0.02f+0.5f)%scx
                }
                view2.scaleY=view1.scaleX.let {sx->
                    (sx+0.02f+0.5f)%scx
                }

                view3.scaleX=view1.scaleX.let {sx->
                    (sx+0.02f+1f)%scx
                }
                view3.scaleY=view1.scaleX.let {sx->
                    (sx+0.02f+1f)%scx
                }

                view4.scaleX=view1.scaleX.let {sx->
                    (sx+0.02f+2f)%scx
                }
                view4.scaleY=view1.scaleX.let {sx->
                    (sx+0.02f+2f)%scx
                }

//                view5.scaleX=view1.scaleX.let {sx->
//                    (sx+0.02f+3f)%scx
//                }
//                view5.scaleY=view1.scaleX.let {sx->
//                    (sx+0.02f+3f)%scx
//                }

                view6.scaleX=view1.scaleX.let {sx->
                    (sx+0.02f+3.5f)%scx
                }
                view6.scaleY=view1.scaleX.let {sx->
                    (sx+0.02f+3.5f)%scx
                }
            }
            valueAnimator?.duration=3000
            valueAnimator?.interpolator=LinearInterpolator()
            valueAnimator?.repeatMode=ValueAnimator.RESTART
            valueAnimator?.repeatCount=ValueAnimator.INFINITE
            valueAnimator?.start()
        }
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }

    override fun onRetry(context: Context?, view: View?): Boolean {
        return super.onRetry(context, view)
    }

    override fun onDetach() {
        super.onDetach()
        valueAnimator?.cancel()
    }
}