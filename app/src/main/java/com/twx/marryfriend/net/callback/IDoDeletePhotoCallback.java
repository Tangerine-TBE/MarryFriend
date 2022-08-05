package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.DeletePhotoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoDeletePhotoCallback extends IBaseCallback {

    void onDoDeletePhotoSuccess(DeletePhotoBean deletePhotoBean);

    void onDoDeletePhotoError();

}
