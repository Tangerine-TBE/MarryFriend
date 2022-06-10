package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetVerifyCodeCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetVerifyCodePresent extends IBasePresent<IGetVerifyCodeCallback> {

    void getVerifyCode(Map<String, String> info);

}
