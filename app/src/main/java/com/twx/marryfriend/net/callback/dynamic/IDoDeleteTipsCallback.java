package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.CancelFocusBean;
import com.twx.marryfriend.bean.dynamic.DeleteTipsBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoDeleteTipsCallback extends IBaseCallback {

    void onDoDeleteTipsSuccess(DeleteTipsBean deleteTipsBean);

    void onDoDeleteTipsError();

}
