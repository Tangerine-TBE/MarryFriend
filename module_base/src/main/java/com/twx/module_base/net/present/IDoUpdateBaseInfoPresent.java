package com.twx.module_base.net.present;

import com.twx.module_base.net.callback.IDoUpdateBaseInfoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUpdateBaseInfoPresent extends IBasePresent<IDoUpdateBaseInfoCallback> {

    void doUpdateBaseInfo(Map<String, String> info);

}
