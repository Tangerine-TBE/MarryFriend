package com.twx.module_base.net.present;

import com.twx.module_base.net.callback.IDoUpdateVerifyInfoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUpdateVerifyInfoPresent extends IBasePresent<IDoUpdateVerifyInfoCallback> {

    void doUpdateVerifyInfo(Map<String, String> info);

}
