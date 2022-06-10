package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.PhoneLoginBean;
import com.twx.marryfriend.bean.VerifyCodeBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoPhoneLoginCallback extends IBaseCallback {

    void onDoPhoneLoginSuccess(PhoneLoginBean phoneLoginBean);

    void onDoPhoneLoginError();

}
