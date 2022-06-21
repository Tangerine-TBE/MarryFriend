package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.PhoneLoginBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoPhoneLoginCallback extends IBaseCallback {

    void onDoPhoneLoginSuccess(PhoneLoginBean phoneLoginBean);

    void onDoPhoneLoginError();

}
