package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.UploadPhotoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUploadPhotoCallback extends IBaseCallback {

    void onDoUploadPhotoSuccess(UploadPhotoBean uploadPhotoBean);

    void onDoUploadPhotoError();

}
