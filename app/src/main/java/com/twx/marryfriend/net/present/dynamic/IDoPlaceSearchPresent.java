package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IDoPlaceSearchCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoPlaceSearchPresent extends IBasePresent<IDoPlaceSearchCallback> {

    void doPlaceSearch(Map<String, String> info);

}
