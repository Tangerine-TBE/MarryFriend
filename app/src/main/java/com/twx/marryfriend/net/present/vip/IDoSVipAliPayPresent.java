package com.twx.marryfriend.net.present.vip;

import com.twx.marryfriend.net.callback.vip.IDoAliPayCallback;
import com.twx.marryfriend.net.callback.vip.IDoSVipAliPayCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoSVipAliPayPresent extends IBasePresent<IDoSVipAliPayCallback> {

    void doSVipAliPay(Map<String, String> info);

}
