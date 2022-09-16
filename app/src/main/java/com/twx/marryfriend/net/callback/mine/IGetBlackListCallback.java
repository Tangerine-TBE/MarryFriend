package com.twx.marryfriend.net.callback.mine;

import com.twx.marryfriend.bean.vip.BlackListBean;
import com.twx.marryfriend.bean.vip.CoinRecordBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetBlackListCallback extends IBaseCallback {

    void onGetBlackListSuccess(BlackListBean blackListBean);

    void onGetBlackListCodeError();

}
