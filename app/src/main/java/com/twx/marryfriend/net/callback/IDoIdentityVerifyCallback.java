package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.AccessTokenBean;
import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.IdentityVerifyBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoIdentityVerifyCallback extends IBaseCallback {

    void onDoIdentityVerifySuccess(IdentityVerifyBean identityVerifyBean);

    void onDoIdentityVerifyError();

}
