package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IGetTrendFocusCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetTrendFocusPresent extends IBasePresent<IGetTrendFocusCallback> {

    void getTrendFocus(Map<String, String> info);

}
