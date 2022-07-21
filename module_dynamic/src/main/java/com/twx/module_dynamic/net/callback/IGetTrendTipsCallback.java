package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.TrendSaloonBean;
import com.twx.module_dynamic.bean.TrendTipBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetTrendTipsCallback extends IBaseCallback {

    void onGetTrendTipsSuccess(TrendTipBean trendTipBean);

    void onGetTrendTipsError();

}
