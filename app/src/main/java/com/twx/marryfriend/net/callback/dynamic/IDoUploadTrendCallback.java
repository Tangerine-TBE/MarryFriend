package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.UploadTrendBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUploadTrendCallback extends IBaseCallback {

    void onDoUploadTrendSuccess(UploadTrendBean uploadTrendBean);

    void onDoUploadTrendError();

}
