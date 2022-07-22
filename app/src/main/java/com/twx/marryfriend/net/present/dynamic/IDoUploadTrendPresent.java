package com.twx.marryfriend.net.present.dynamic;



import com.twx.marryfriend.net.callback.dynamic.IDoUploadTrendCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUploadTrendPresent extends IBasePresent<IDoUploadTrendCallback> {

    void doUploadTrend(Map<String, String> info);

}
