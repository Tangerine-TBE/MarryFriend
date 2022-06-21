package com.twx.module_base.net.present;

import com.twx.module_base.net.callback.IDoUploadPhotoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUploadPhotoPresent extends IBasePresent<IDoUploadPhotoCallback> {

    void doUploadPhoto(Map<String, String> info);

}
