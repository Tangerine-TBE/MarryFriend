package com.twx.marryfriend.net.present;

import androidx.annotation.AnyRes;

import com.twx.marryfriend.net.callback.IDoUpdateBaseInfoCallback;
import com.twx.marryfriend.net.callback.IDoUploadPhotoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUploadPhotoPresent extends IBasePresent<IDoUploadPhotoCallback> {

    void doUploadPhoto(Map<String, String> info);

}
