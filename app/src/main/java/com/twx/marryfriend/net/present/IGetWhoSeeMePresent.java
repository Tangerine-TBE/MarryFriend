package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetCityCallback;
import com.twx.marryfriend.net.callback.IGetWhoSeeMeCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetWhoSeeMePresent extends IBasePresent<IGetWhoSeeMeCallback> {

    void getWhoSeeMe(Map<String, String> info);

}
