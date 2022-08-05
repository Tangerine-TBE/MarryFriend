package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.UploadAvatarBean;
import com.twx.marryfriend.bean.UploadPhotoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUploadAvatarCallback extends IBaseCallback {

    void onDoUploadAvatarSuccess(UploadAvatarBean uploadAvatarBean);

    void onDoUploadAvatarError();

}
