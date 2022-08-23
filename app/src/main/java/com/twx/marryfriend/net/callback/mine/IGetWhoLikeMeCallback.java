package com.twx.marryfriend.net.callback.mine;

import com.twx.marryfriend.bean.mine.WhoFocusMeBean;
import com.twx.marryfriend.bean.mine.WhoLikeMeBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetWhoLikeMeCallback extends IBaseCallback {

    void onGetWhoLikeMeSuccess(WhoLikeMeBean whoLikeMeBean);

    void onGetWhoLikeMeCodeError();

}
