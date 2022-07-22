package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.DeleteTrendBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoDeleteTrendCallback extends IBaseCallback {

    void onDoDeleteTrendSuccess(DeleteTrendBean deleteTrendBean);

    void onDoDeleteTrendError();

}
