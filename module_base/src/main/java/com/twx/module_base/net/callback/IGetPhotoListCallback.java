package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.PhotoListBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetPhotoListCallback extends IBaseCallback {

    void onGetPhotoListSuccess(PhotoListBean photoListBean);

    void onGetPhotoListError();

}
