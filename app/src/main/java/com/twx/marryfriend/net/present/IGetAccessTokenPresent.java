package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetAccessTokenCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/20
 */
public interface IGetAccessTokenPresent extends IBasePresent<IGetAccessTokenCallback> {

    void getAccessToken(Map<String, String> info);

}
