package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.BaseInfoUpdateBean;
import com.twx.marryfriend.bean.UploadPhotoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUploadPhotoCallback extends IBaseCallback {

    void onDoUploadPhotoSuccess(UploadPhotoBean uploadPhotoBean);

    void onDoUploadPhotoError();

}
