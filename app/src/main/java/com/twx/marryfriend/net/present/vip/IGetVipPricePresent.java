package com.twx.marryfriend.net.present.vip;

import com.twx.marryfriend.net.callback.IGetBanCallback;
import com.twx.marryfriend.net.callback.vip.IGetVipPriceCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetVipPricePresent extends IBasePresent<IGetVipPriceCallback> {

    void getVipPrice(Map<String, String> info);

}
