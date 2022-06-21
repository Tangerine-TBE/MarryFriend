package com.twx.module_base.net.present;



import com.twx.module_base.net.callback.IDoIdentityVerifyCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoIdentityVerifyPresent extends IBasePresent<IDoIdentityVerifyCallback> {

    void doIdentityVerify(Map<String, String> info);

}
