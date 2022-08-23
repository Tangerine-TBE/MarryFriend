package com.twx.marryfriend.utils.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.blankj.utilcode.util.ToastUtils;

/**
 * @author: Administrator
 * @date: 2022/8/22
 */
public class CustomScrollView extends NestedScrollView {

    /**
     * Runnable延迟执行的时间
     */
    private long delayMillis = 100;

    /**
     * 上次滑动的时间
     */
    private long lastScrollUpdate = -1;

    private OnScrollEndListener mOnScrollEndListener;

    private Runnable scrollerTask = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastScrollUpdate) > 100) {
                lastScrollUpdate = -1;
                onScrollEnd();
            } else {
                postDelayed(this, delayMillis);
            }
        }
    };

    public CustomScrollView(@NonNull Context context) {
        this(context, null);
    }

    public CustomScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (lastScrollUpdate == -1) {
            onScrollStart();
            postDelayed(scrollerTask, delayMillis);
        }
        // 更新ScrollView的滑动时间
        lastScrollUpdate = System.currentTimeMillis();
    }


    /**
     * 滑动开始
     */
    private void onScrollStart() {
    }
    public interface OnScrollEndListener {
        void onScrollEnd();
    }

    public void setOnScrollEndListener(OnScrollEndListener listener) {
        mOnScrollEndListener = listener;
    }

    /**
     * 滑动结束
     */
    private void onScrollEnd() {
        mOnScrollEndListener.onScrollEnd();
    }

}

