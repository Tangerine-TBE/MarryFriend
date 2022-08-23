package com.twx.marryfriend.net.callback.vip;

import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.vip.AliPayBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoAliPayCallback extends IBaseCallback {

    void onDoAliPaySuccess(AliPayBean aliPayBean);

    void onDoAliPayError();

}
