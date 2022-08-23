package com.twx.marryfriend.net.callback.mine;

import com.twx.marryfriend.bean.mine.WhoSeeMeBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetWhoSeeMeCallback extends IBaseCallback {

    void onGetWhoSeeMeSuccess(WhoSeeMeBean whoSeeMeBean);

    void onGetWhoSeeMeCodeError();

}
