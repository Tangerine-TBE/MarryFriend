package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IDoDeleteTrendCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoDeleteTrendPresent extends IBasePresent<IDoDeleteTrendCallback> {

    void doDeleteTrend(Map<String, String> info);

}
