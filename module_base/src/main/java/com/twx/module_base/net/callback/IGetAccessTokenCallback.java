package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.AccessTokenBean;

/**
 * @author: Administrator
 * @date: 2022/5/20
 */
public interface IGetAccessTokenCallback extends IBaseCallback {

    void onGetAccessTokenSuccess(AccessTokenBean accessTokenBean);

    void onGetAccessTokenFail();

}
