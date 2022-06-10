package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetBanCallback;
import com.twx.marryfriend.net.callback.IGetSchoolCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetBanPresent extends IBasePresent<IGetBanCallback> {

    void getBan(Map<String, String> info);

}
