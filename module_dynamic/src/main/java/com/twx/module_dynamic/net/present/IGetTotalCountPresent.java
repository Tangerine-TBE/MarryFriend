package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IGetTotalCountCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetTotalCountPresent extends IBasePresent<IGetTotalCountCallback> {

    void getTotalCount(Map<String, String> info);

}
