package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.VerifyCodeBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetVerifyCodeCallback extends IBaseCallback {

    void onGetVerifyCodeSuccess(VerifyCodeBean verifyCodeBean);

    void onGetVerifyCodeError();

}
