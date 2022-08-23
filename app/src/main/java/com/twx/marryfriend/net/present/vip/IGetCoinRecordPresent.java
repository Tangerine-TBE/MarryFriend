package com.twx.marryfriend.net.present.vip;

import com.twx.marryfriend.net.callback.vip.IGetCoinPriceCallback;
import com.twx.marryfriend.net.callback.vip.IGetCoinRecordCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetCoinRecordPresent extends IBasePresent<IGetCoinRecordCallback> {

    void getCoinRecord(Map<String, String> info, Integer page);

}
