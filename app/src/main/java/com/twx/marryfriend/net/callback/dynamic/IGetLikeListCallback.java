package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.LikeListBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetLikeListCallback extends IBaseCallback {

    void onGetLikeListSuccess(LikeListBean likeListBean);

    void onGetLikeListCodeError();

}
