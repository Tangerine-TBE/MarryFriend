package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.TrendSaloonBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetTrendSaloonCallback extends IBaseCallback {

    void onGetTrendSaloonSuccess(TrendSaloonBean trendSaloonBean);

    void onGetTrendSaloonError();

}
