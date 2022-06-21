package com.twx.module_base.net.present;

import com.twx.module_base.net.callback.IGetAccessTokenCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/20
 */
public interface IGetAccessTokenPresent extends IBasePresent<IGetAccessTokenCallback> {

    void getAccessToken(Map<String, String> info);

}
