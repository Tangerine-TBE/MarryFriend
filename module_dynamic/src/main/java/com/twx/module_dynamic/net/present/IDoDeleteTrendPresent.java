package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IDoCheckTrendCallback;
import com.twx.module_dynamic.net.callback.IDoDeleteTrendCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoDeleteTrendPresent extends IBasePresent<IDoDeleteTrendCallback> {

    void doDeleteTrend(Map<String, String> info);

}
