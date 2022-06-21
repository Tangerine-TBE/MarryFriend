package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetPhotoListCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetPhotoListPresent extends IBasePresent<IGetPhotoListCallback> {

    void getPhotoList(Map<String, String> info);

}
