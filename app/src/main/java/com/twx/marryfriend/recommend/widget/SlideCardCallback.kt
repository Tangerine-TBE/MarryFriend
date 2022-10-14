package com.twx.marryfriend.recommend.widget

import android.graphics.Canvas
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.twx.marryfriend.R
import com.twx.marryfriend.base.BaseConstant
import com.xyzz.myutils.show.iLog
import java.lang.Math.abs
import kotlin.random.Random

class SlideCardCallback : ItemTouchHelper.SimpleCallback(0,12) {
    var removeItemAction:((Int)->Unit)?=null
    var swipeEnabled=true

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    private val toast by lazy {
        Toast.makeText(BaseConstant.application,"请稍后再左右滑动",Toast.LENGTH_SHORT)
    }
    override fun isItemViewSwipeEnabled(): Boolean {
        if (!swipeEnabled){
            toast.show()
        }
        return swipeEnabled
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        viewHolder.itemView.rotation=0f
        removeItemAction?.invoke(direction)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (actionState== ItemTouchHelper.ACTION_STATE_SWIPE){
            val widthPixels= viewHolder.itemView.context?.resources?.displayMetrics?.widthPixels?:return
            val alpha= kotlin.math.abs(dX / (widthPixels.toFloat() / 5))
            if (dX<0){//不喜欢
                viewHolder.itemView.findViewById<View>(R.id.homeDislike)?.alpha=alpha
            }else{//喜欢
                viewHolder.itemView.findViewById<View>(R.id.homeLike)?.alpha=alpha
            }
        }

        val fraction=abs(dX/(recyclerView.width/2)).let {
            if (it>1f){
                1f
            }else{
                it
            }
        }
        for (i in recyclerView.childCount-1 downTo 0 ){
            val view=recyclerView.getChildAt((recyclerView.childCount-1)-i)
            if (i==0){
                if (fraction==1f&&view.rotation==0f){
                    continue
                }
                view.rotation= if (dX<0){
                    10f
                }else{
                    -10f
                }*fraction
            }else{
                view.rotation=0f
                view.translationY=-(CardConfig.TRANS_Y_GAP * (i-fraction))
                view.scaleX=1 - CardConfig.SCALE_GAP * (i-fraction)
                view.scaleY=1 - CardConfig.SCALE_GAP * (i-fraction)
            }
        }
    }

    // 回弹时间
    override fun getAnimationDuration(
        recyclerView: RecyclerView,
        animationType: Int,
        animateDx: Float,
        animateDy: Float
    ): Long {
        return 300L
    }

    // 回弹距离
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.3f
    }
}