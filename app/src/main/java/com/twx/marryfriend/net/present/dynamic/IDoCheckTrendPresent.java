package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IDoCheckTrendCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoCheckTrendPresent extends IBasePresent<IDoCheckTrendCallback> {

    void doCheckTrend(Map<String, String> info);

}
