package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.AccessTokenBean;

/**
 * @author: Administrator
 * @date: 2022/5/20
 *
 * 身份证姓名与号验证
 *
 */
public interface IGetLifeAccessTokenCallback extends IBaseCallback {

    void onGetLifeAccessTokenSuccess(AccessTokenBean accessTokenBean);

    void onGetLifeAccessTokenFail();

}
