package com.twx.marryfriend.net.callback.vip;

import com.twx.marryfriend.bean.vip.CoinPriceBean;
import com.twx.marryfriend.bean.vip.CoinRecordBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetCoinRecordCallback extends IBaseCallback {

    void onGetCoinRecordSuccess(CoinRecordBean coinRecordBean);

    void onGetCoinRecordCodeError();

}
