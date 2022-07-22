package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.PlaceSearchBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoPlaceSearchCallback extends IBaseCallback {

    void onDoPlaceSearchSuccess(PlaceSearchBean placeSearchBean);

    void onDoPlaceSearchError();

}
