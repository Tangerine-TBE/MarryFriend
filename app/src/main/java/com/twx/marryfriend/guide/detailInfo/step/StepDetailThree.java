package com.twx.marryfriend.guide.detailInfo.step;

import android.app.Activity;
import android.view.View;

import com.twx.marryfriend.guide.baseInfo.step.RegisterStep;

/**
 * Created by zhaojie on 2015/4/11.
 */
public class StepDetailThree extends RegisterStep {

    public StepDetailThree(Activity activity, View contentView) {
        super(activity, contentView);
    }

    @Override
    public View findViewById(int id) {
        return super.findViewById(id);
    }

    @Override
    public void initViews() { }

    @Override
    public void initEvents() { }

    @Override
    public boolean validate() {
        return true;
    }


}
