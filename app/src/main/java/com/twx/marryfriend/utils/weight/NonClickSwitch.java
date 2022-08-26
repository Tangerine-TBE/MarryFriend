package com.twx.marryfriend.utils.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Switch;

import androidx.annotation.Nullable;

/**
 * @author: Administrator
 * @date: 2022/8/24
 */

public class NonClickSwitch extends Switch {
    OnClickListener l;

    public NonClickSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP)
            if (l != null)
                l.onClick(this);
        return true;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.l = l;
    }
}
