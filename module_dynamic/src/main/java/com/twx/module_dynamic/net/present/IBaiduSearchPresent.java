package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IBaiduSearchCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IBaiduSearchPresent extends IBasePresent<IBaiduSearchCallback> {

    void doBaiduSearch(Map<String, String> info);

}
