package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoAutoLoginPresent extends IBasePresent<IDoAutoLoginCallback> {

    void doAutoLogin(Map<String, String> info);

}
