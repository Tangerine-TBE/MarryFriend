package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IGetTrendFocusCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetTrendFocusPresent extends IBasePresent<IGetTrendFocusCallback> {

    void getTrendFocus(Map<String, String> info);

}
