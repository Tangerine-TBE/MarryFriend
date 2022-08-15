package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.DemandAddressBean;
import com.twx.marryfriend.bean.PlusDemandAddressBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoGetDemandAddressCallback extends IBaseCallback {

    void onDoGetDemandAddressSuccess(DemandAddressBean demandAddressBean);

    void onDoGetDemandAddressError();

}
