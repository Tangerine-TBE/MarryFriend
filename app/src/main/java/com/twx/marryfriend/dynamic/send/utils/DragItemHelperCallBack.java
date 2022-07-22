package com.twx.marryfriend.dynamic.send.utils;

import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class DragItemHelperCallBack extends ItemTouchHelper.Callback {

    @Override
    public int getMovementFlags(RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags;
        if(recyclerView.getLayoutManager() instanceof GridLayoutManager){
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        // 支持左右滑动(删除)操作, swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END
        final int swipeFlags = 0;

        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(@NotNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        Log.i("guo","onmove");

        //被按下拖拽时候的position
        int fromPosition = viewHolder.getAdapterPosition();
        //当前拖拽到的item的position
        int toPosition = target.getAdapterPosition();



        //回调到adapter 当中处理移动过程中,数据变更的逻辑,以及更新UI

        return true;
    }

    @Override
    public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
        Log.i("guo","onSwiped");
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        Log.i("guo","onSelectedChanged");

        // 不在闲置状态
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {

        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
        Log.i("guo","clearView");

        super.clearView(recyclerView, viewHolder);
    }
}