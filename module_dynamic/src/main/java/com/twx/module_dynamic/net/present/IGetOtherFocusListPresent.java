package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IGetMyFocusListCallback;
import com.twx.module_dynamic.net.callback.IGetOtherFocusListCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetOtherFocusListPresent extends IBasePresent<IGetOtherFocusListCallback> {

    void getOtherFocus(Map<String, String> info);

}
