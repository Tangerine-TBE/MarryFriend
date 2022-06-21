package com.twx.module_base.net.present;

import com.twx.module_base.net.callback.IDoUpdateGreetInfoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUpdateGreetInfoPresent extends IBasePresent<IDoUpdateGreetInfoCallback> {

    void doUpdateGreetInfo(Map<String, String> info);

}
