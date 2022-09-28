package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetCityCallback;
import com.twx.marryfriend.net.callback.IGetGreetInfoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetGreetInfoPresent extends IBasePresent<IGetGreetInfoCallback> {

    void getGreetInfo(Map<String, String> info);

}
