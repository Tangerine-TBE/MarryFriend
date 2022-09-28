package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.LikeCancelBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoFocusLikeCancelCallback extends IBaseCallback {

    void onDoFocusLikeCancelSuccess(LikeCancelBean likeCancelBean);

    void onFocusLikeCancelError();

}
