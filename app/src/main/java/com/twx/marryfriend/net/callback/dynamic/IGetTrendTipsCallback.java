package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.TrendTipBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetTrendTipsCallback extends IBaseCallback {

    void onGetTrendTipsSuccess(TrendTipBean trendTipBean);

    void onGetTrendTipsError();

}
