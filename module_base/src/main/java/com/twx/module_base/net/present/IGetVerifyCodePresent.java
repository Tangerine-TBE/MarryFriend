package com.twx.module_base.net.present;

import com.twx.module_base.net.callback.IGetVerifyCodeCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetVerifyCodePresent extends IBasePresent<IGetVerifyCodeCallback> {

    void getVerifyCode(Map<String, String> info);

}
