package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.PlusFocusBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoPlusFocusCallback extends IBaseCallback {

    void onDoPlusFocusSuccess(PlusFocusBean plusFocusBean);

    void onDoPlusFocusError();

}
