package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetAccessTokenCallback;
import com.twx.marryfriend.net.callback.IGetIdAccessTokenCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/20
 */
public interface IGetIdAccessTokenPresent extends IBasePresent<IGetIdAccessTokenCallback> {

    void getAccessToken(Map<String, String> info);

}
