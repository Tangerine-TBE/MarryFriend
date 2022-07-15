package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.IDoTextVerifyCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoTextVerifyPresent extends IBasePresent<IDoTextVerifyCallback> {

    void doTextVerify(Map<String, String> info);

}
