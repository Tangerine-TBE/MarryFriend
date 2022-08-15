package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.FaceVerifyBean;
import com.twx.marryfriend.bean.PlusDemandAddressBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoPlusDemandAddressCallback extends IBaseCallback {

    void onDoPlusDemandAddressSuccess(PlusDemandAddressBean plusDemandAddressBean);

    void onDoPlusDemandAddressError();

}
