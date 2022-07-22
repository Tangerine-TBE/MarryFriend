package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.TrendSaloonBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetTrendSaloonCallback extends IBaseCallback {

    void onGetTrendSaloonSuccess(TrendSaloonBean trendSaloonBean);

    void onGetTrendSaloonError();

}
