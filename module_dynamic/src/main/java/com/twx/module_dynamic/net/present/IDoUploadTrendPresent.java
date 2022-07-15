package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IDoPlaceSearchCallback;
import com.twx.module_dynamic.net.callback.IDoUploadTrendCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUploadTrendPresent extends IBasePresent<IDoUploadTrendCallback> {

    void doUploadTrend(Map<String, String> info);

}
