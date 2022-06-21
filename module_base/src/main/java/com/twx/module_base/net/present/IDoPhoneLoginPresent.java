package com.twx.module_base.net.present;


import com.twx.module_base.net.callback.IDoPhoneLoginCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoPhoneLoginPresent extends IBasePresent<IDoPhoneLoginCallback> {

    void doPhoneLogin(Map<String, String> info);

}
