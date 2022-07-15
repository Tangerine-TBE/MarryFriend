package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.TrendFocusBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetTrendFocusCallback extends IBaseCallback {

    void onGetTrendFocusSuccess(TrendFocusBean trendFocusBean);

    void onGetTrendFocusError();

}
