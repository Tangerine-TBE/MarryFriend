package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoPlusDemandAddressCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoPlusDemandAddressPresent extends IBasePresent<IDoPlusDemandAddressCallback> {

    void doPlusDemandAddress(Map<String, String> info);

}
