package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.TrendFocusBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetTrendFocusCallback extends IBaseCallback {

    void onGetTrendFocusSuccess(TrendFocusBean trendFocusBean);

    void onGetTrendFocusError();

}
