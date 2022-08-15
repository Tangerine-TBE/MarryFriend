package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoGetDemandAddressCallback;
import com.twx.marryfriend.net.callback.IDoPlusDemandAddressCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoGetDemandAddressPresent extends IBasePresent<IDoGetDemandAddressCallback> {

    void doGetDemandAddress(Map<String, String> info);

}
