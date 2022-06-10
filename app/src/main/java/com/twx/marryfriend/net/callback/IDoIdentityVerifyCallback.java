package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.AccessTokenBean;
import com.twx.marryfriend.bean.AutoLoginBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoIdentityVerifyCallback extends IBaseCallback {

    void onDoIdentityVerifySuccess(AccessTokenBean accessTokenBean);

    void onDoIdentityVerifyError();

}
