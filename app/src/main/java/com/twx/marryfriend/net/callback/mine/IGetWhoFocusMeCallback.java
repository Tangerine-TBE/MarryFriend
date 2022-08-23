package com.twx.marryfriend.net.callback.mine;

import com.twx.marryfriend.bean.mine.WhoFocusMeBean;
import com.twx.marryfriend.bean.mine.WhoSeeMeBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetWhoFocusMeCallback extends IBaseCallback {

    void onGetWhoFocusMeSuccess(WhoFocusMeBean whoFocusMeBean);

    void onGetWhoFocusMeCodeError();

}
