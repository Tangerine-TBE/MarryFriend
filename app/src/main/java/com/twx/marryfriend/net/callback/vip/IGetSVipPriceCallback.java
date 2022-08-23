package com.twx.marryfriend.net.callback.vip;

import com.twx.marryfriend.bean.vip.VipPriceBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetSVipPriceCallback extends IBaseCallback {

    void onGetSVipPriceSuccess(VipPriceBean vipPriceBean);

    void onGetSVipPriceCodeError();

}
