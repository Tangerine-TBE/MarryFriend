package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoUpdateMoreInfoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUpdateMoreInfoPresent extends IBasePresent<IDoUpdateMoreInfoCallback> {

    void doUpdateMoreInfo(Map<String, String> info);

}
