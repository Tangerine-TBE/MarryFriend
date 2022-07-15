package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.PlaceSearchBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoPlaceSearchCallback extends IBaseCallback {

    void onDoPlaceSearchSuccess(PlaceSearchBean placeSearchBean);

    void onDoPlaceSearchError();

}
