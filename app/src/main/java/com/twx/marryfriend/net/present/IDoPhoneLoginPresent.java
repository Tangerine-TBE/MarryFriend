package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoPhoneLoginCallback;
import com.twx.marryfriend.net.callback.IGetVerifyCodeCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoPhoneLoginPresent extends IBasePresent<IDoPhoneLoginCallback> {

    void doPhoneLogin(Map<String, String> info);

}
