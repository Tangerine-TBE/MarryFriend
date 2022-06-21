package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.PhotoListBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetPhotoListCallback extends IBaseCallback {

    void onGetPhotoListSuccess(PhotoListBean photoListBean);

    void onGetPhotoListError();

}
