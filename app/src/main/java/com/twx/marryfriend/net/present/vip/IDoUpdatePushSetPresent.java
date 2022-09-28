package com.twx.marryfriend.net.present.vip;

import com.twx.marryfriend.net.callback.vip.IDoUpdatePushSetCallback;
import com.twx.marryfriend.net.callback.vip.IDoUpdateTokenCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUpdatePushSetPresent extends IBasePresent<IDoUpdatePushSetCallback> {

    void doUpdatePushSet(Map<String, String> info);

}
