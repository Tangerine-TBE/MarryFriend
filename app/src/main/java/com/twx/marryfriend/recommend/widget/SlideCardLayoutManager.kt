package com.twx.marryfriend.recommend.widget

import androidx.recyclerview.widget.RecyclerView

class SlideCardLayoutManager:RecyclerView.LayoutManager() {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        detachAndScrapAttachedViews(recycler?:return)

        // 总共的item个数：8个
        val itemCount = itemCount

        val bottomPosition = if (itemCount > CardConfig.MAX_SHOW_COUNT) {
            CardConfig.MAX_SHOW_COUNT-1
        } else {
            itemCount - 1
        }

        for (i in bottomPosition downTo  0) {
            // 那View --》 复用
            val view = recycler.getViewForPosition(i)
            addView(view)

            // onMeasure
            measureChildWithMargins(view, 0, 0)
            val widthSpace = width - getDecoratedMeasuredWidth(view)
            val heightSpace = height - getDecoratedMeasuredHeight(view)

            // onLayout -- 布局所有子View
            layoutDecoratedWithMargins(
                view, widthSpace / 2, heightSpace / 2,
                widthSpace / 2 + getDecoratedMeasuredWidth(view),
                heightSpace / 2 + getDecoratedMeasuredHeight(view)
            )

            // 对子View进行缩放平移处理
            if (i== CardConfig.MAX_SHOW_COUNT - 1){
                view.translationY = -(CardConfig.TRANS_Y_GAP * (i-1)).toFloat()
                view.scaleX = 1 - CardConfig.SCALE_GAP * (i-1)
                view.scaleY = 1 - CardConfig.SCALE_GAP * (i-1)
            }else{
                view.translationY = -(CardConfig.TRANS_Y_GAP * i).toFloat()
                view.scaleX = 1 - CardConfig.SCALE_GAP * i
                view.scaleY = 1 - CardConfig.SCALE_GAP * i
            }
        }
    }

    override fun canScrollVertically(): Boolean {
        return false
    }
}