package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.SearchBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IBaiduSearchCallback extends IBaseCallback {

    void onBaiduSearchSuccess(SearchBean searchBean);

    void onBaiduSearchError();

}
