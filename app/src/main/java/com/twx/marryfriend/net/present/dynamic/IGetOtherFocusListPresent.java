package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IGetOtherFocusListCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetOtherFocusListPresent extends IBasePresent<IGetOtherFocusListCallback> {

    void getOtherFocus(Map<String, String> info);

}
