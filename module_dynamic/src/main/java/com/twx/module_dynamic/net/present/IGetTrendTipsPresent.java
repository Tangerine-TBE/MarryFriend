package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IGetTrendSaloonCallback;
import com.twx.module_dynamic.net.callback.IGetTrendTipsCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetTrendTipsPresent extends IBasePresent<IGetTrendTipsCallback> {

    void getTrendTips(Map<String, String> info, Integer page);

}
