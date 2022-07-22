package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.CheckTrendBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoCheckTrendCallback extends IBaseCallback {

    void onDoCheckTrendSuccess(CheckTrendBean checkTrendBean);

    void onDoCheckTrendError();

}
