package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoUpdateDemandInfoCallback;
import com.twx.marryfriend.net.callback.IDoUpdateGreetInfoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUpdateGreetInfoPresent extends IBasePresent<IDoUpdateGreetInfoCallback> {

    void doUpdateGreetInfo(Map<String, String> info);

}
