package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.TextVerifyBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoTextVerifyCallback extends IBaseCallback {

    void onDoTextVerifySuccess(TextVerifyBean textVerifyBean);

    void onDoTextVerifyError();

}
