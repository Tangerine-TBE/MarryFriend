package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.CityBean;
import com.twx.marryfriend.bean.WhoSeeMeBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetWhoSeeMeCallback extends IBaseCallback {

    void onGetWhoSeeMeSuccess(WhoSeeMeBean whoSeeMeBean);

    void onGetWhoSeeMeCodeError();

}
