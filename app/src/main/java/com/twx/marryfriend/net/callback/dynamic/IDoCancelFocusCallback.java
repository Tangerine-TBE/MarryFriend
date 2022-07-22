package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.CancelFocusBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoCancelFocusCallback extends IBaseCallback {

    void onDoCancelFocusSuccess(CancelFocusBean cancelFocusBean);

    void onDoCancelFocusError();

}
