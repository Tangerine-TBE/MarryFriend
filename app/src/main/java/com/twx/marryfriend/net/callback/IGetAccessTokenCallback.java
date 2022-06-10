package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.AccessTokenBean;

/**
 * @author: Administrator
 * @date: 2022/5/20
 */
public interface IGetAccessTokenCallback extends IBaseCallback {

    void onGetAccessTokenSuccess(AccessTokenBean accessTokenBean);

    void onGetAccessTokenFail();

}
