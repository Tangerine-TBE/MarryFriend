package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.IdentityVerifyBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoIdentityVerifyCallback extends IBaseCallback {

    void onDoIdentityVerifySuccess(IdentityVerifyBean identityVerifyBean);

    void onDoIdentityVerifyError();

}
