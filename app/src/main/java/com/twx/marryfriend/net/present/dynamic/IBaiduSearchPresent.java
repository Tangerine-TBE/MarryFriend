package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IBaiduSearchCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IBaiduSearchPresent extends IBasePresent<IBaiduSearchCallback> {

    void doBaiduSearch(Map<String, String> info);

}
