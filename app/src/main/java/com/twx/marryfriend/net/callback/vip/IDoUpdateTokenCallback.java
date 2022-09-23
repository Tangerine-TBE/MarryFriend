package com.twx.marryfriend.net.callback.vip;

import com.twx.marryfriend.bean.vip.AliPayBean;
import com.twx.marryfriend.bean.vip.UpdateTokenBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateTokenCallback extends IBaseCallback {

    void onDoUpdateTokenSuccess(UpdateTokenBean updateTokenBean);

    void onDoUpdateTokenError();

}
