package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.VerifyCodeBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetVerifyCodeCallback extends IBaseCallback {

    void onGetVerifyCodeSuccess(VerifyCodeBean verifyCodeBean);

    void onGetVerifyCodeError();

}
