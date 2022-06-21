package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.AutoLoginBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoAutoLoginCallback extends IBaseCallback {

    void onDoAutoLoginSuccess(AutoLoginBean autoLoginBean);

    void onDoAutoLoginError();

}
