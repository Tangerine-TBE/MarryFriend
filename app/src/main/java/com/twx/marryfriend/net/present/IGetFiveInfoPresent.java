package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetFiveInfoCallback;
import com.twx.marryfriend.net.callback.IGetVerifyCodeCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetFiveInfoPresent extends IBasePresent<IGetFiveInfoCallback> {

    void getFiveInfo(Map<String, String> info);

}
