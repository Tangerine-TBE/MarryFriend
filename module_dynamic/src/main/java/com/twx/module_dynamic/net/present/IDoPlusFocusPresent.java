package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IDoPlusFocusCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoPlusFocusPresent extends IBasePresent<IDoPlusFocusCallback> {

    void doPlusFocusOther(Map<String, String> info);

}
