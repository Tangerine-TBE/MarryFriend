package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetIdAccessTokenCallback;
import com.twx.marryfriend.net.callback.IGetLifeAccessTokenCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/20
 */
public interface IGetLifeAccessTokenPresent extends IBasePresent<IGetLifeAccessTokenCallback> {

    void getAccessToken(Map<String, String> info);

}
