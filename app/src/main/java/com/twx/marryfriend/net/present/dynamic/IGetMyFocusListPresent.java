package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IGetMyFocusListCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetMyFocusListPresent extends IBasePresent<IGetMyFocusListCallback> {

    void getMyFocus(Map<String, String> info);

}
