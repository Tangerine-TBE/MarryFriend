package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IDoCancelFocusCallback;
import com.twx.module_dynamic.net.callback.IDoPlusFocusCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoCancelFocusPresent extends IBasePresent<IDoCancelFocusCallback> {

    void doCancelFocusOther(Map<String, String> info);

}
