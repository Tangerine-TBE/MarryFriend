package com.twx.marryfriend.net.present.vip;

import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.vip.IDoAliPayCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoAliPayPresent extends IBasePresent<IDoAliPayCallback> {

    void doAliPay(Map<String, String> info);

}
