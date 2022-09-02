package com.twx.marryfriend.net.callback.mine;

import com.twx.marryfriend.bean.mine.FourTotalBean;
import com.twx.marryfriend.bean.mine.WhoSeeMeBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetFourTotalCallback extends IBaseCallback {

    void onGetFourTotalSuccess(FourTotalBean fourTotalBean);

    void onGetFourTotalError();

}
