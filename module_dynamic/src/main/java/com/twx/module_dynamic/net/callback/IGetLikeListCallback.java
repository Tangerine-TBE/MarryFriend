package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.LikeListBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetLikeListCallback extends IBaseCallback {

    void onGetLikeListSuccess(LikeListBean likeListBean);

    void onGetLikeListCodeError();

}
