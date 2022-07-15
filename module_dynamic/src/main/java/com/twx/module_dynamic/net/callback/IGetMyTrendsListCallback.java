package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.MyTrendsListBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetMyTrendsListCallback extends IBaseCallback {

    void onGetMyTrendsListSuccess(MyTrendsListBean myTrendsListBean);

    void onGetMyTrendsListCodeError();

}
