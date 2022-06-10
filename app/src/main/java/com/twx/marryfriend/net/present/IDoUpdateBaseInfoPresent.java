package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.IDoUpdateBaseInfoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUpdateBaseInfoPresent extends IBasePresent<IDoUpdateBaseInfoCallback> {

    void doUpdateBaseInfo(Map<String, String> info);

}
