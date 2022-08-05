package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.IDoDeletePhotoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoDeletePhotoPresent extends IBasePresent<IDoDeletePhotoCallback> {

    void doDeletePhoto(Map<String, String> info);

}
