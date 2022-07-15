package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IGetMyFocusListCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetMyFocusListPresent extends IBasePresent<IGetMyFocusListCallback> {

    void getMyFocus(Map<String, String> info);

}
