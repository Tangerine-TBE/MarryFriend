package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IGetTrendTipsCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetTrendTipsPresent extends IBasePresent<IGetTrendTipsCallback> {

    void getTrendTips(Map<String, String> info, Integer page);

}
