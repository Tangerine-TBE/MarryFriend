package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.MyTrendsListBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetMyTrendsListCallback extends IBaseCallback {

    void onGetMyTrendsListSuccess(MyTrendsListBean myTrendsListBean);

    void onGetMyTrendsListCodeError();

}
