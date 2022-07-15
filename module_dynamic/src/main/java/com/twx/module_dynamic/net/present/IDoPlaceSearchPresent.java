package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IDoPlaceSearchCallback;
import com.twx.module_dynamic.net.callback.IGetMyTrendsListCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoPlaceSearchPresent extends IBasePresent<IDoPlaceSearchCallback> {

    void doPlaceSearch(Map<String, String> info);

}
