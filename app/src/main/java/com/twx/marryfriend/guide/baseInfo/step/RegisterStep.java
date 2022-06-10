package com.twx.marryfriend.guide.baseInfo.step;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by zhaojie on 2015/4/11.
 */
public abstract class RegisterStep {
    public interface OnNextActionListener {
        void next();

        void submit();
    }

    protected Activity mActivity;
    protected Context mContext;

    private View mContentView;

    protected OnNextActionListener mOnNextActionListener;

    public RegisterStep(Activity activity, View contentView) {
        mActivity = activity;
        mContext = (Context) mActivity;
        mContentView = contentView;

        initViews();
        initEvents();
    }

    public abstract void initViews();

    public abstract void initEvents();

    public abstract boolean validate();

    public View findViewById(int id) {
        return mContentView.findViewById(id);
    }

    public void doPrevious() { }

    public void doNext() {
        mOnNextActionListener.submit();
    }

    public void nextAnimation() { }

    public void preAnimation() { }

    public void setOnNextActionListener(OnNextActionListener onNextActionListener) {
        mOnNextActionListener = onNextActionListener;
    }
}
