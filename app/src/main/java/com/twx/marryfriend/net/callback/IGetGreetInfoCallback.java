package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.CityBean;
import com.twx.marryfriend.bean.GreetInfoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetGreetInfoCallback extends IBaseCallback {

    void onGetGreetInfoSuccess(GreetInfoBean greetInfoBean);

    void onGetGreetInfoCodeError();

}
