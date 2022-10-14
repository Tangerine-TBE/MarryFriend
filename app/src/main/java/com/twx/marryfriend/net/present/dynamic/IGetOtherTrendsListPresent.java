package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IGetMyTrendsListCallback;
import com.twx.marryfriend.net.callback.dynamic.IGetOtherTrendsListCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetOtherTrendsListPresent extends IBasePresent<IGetOtherTrendsListCallback> {

    void getOtherTrendsList(Map<String, String> info);

}
