package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.BanBean;
import com.twx.marryfriend.bean.CityBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetCityCallback extends IBaseCallback {

    void onGetCitySuccess(CityBean cityBean);

    void onGetCityCodeError();

}
