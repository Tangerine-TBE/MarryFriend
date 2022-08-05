package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoUploadAvatarCallback;
import com.twx.marryfriend.net.callback.IDoUploadPhotoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUploadAvatarPresent extends IBasePresent<IDoUploadAvatarCallback> {

    void doUploadAvatar(Map<String, String> info);

}
