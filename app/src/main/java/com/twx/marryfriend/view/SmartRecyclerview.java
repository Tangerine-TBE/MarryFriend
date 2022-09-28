package com.twx.marryfriend.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: Administrator
 * @date: 2022/9/27
 */
public class SmartRecyclerview extends RecyclerView {
    public SmartRecyclerview(@NonNull Context context) {
        super(context,null);
    }

    public SmartRecyclerview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
    }

    public SmartRecyclerview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup mViewGroup = (ViewGroup) getParent();
        if(null != mViewGroup){
            int mParentHeight = mViewGroup.getHeight();
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), mParentHeight);
        }
    }


}
