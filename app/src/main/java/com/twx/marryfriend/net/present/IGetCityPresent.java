package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetCityCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetCityPresent extends IBasePresent<IGetCityCallback> {

    void getCity(Map<String, String> info);

}
