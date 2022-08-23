package com.twx.marryfriend.net.callback.vip;

import com.twx.marryfriend.bean.BanBean;
import com.twx.marryfriend.bean.vip.VipPriceBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetVipPriceCallback extends IBaseCallback {

    void onGetVipPriceSuccess(VipPriceBean vipPriceBean);

    void onGetVipPriceCodeError();

}
