package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetMeSeeWhoCallback;
import com.twx.marryfriend.net.callback.IGetWhoSeeMeCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetMeSeeWhoPresent extends IBasePresent<IGetMeSeeWhoCallback> {

    void getMeSeeWho(Map<String, String> info);

}
