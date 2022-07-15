package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IGetMyTrendsListCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetMyTrendsListPresent extends IBasePresent<IGetMyTrendsListCallback> {

    void getMyTrendsList(Map<String, String> info, Integer page, Integer size);

}
