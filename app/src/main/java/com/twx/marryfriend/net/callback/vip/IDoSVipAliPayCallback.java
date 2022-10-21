package com.twx.marryfriend.net.callback.vip;

import com.twx.marryfriend.bean.vip.AliPayBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoSVipAliPayCallback extends IBaseCallback {

    void onDoSVipAliPaySuccess(AliPayBean aliPayBean);

    void onDoSVipAliPayError();

}
