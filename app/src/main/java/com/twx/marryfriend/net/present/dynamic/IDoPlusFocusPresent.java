package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IDoPlusFocusCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoPlusFocusPresent extends IBasePresent<IDoPlusFocusCallback> {

    void doPlusFocusOther(Map<String, String> info);

}
