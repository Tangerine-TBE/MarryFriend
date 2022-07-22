package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.LikeClickBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoLikeClickCallback extends IBaseCallback {

    void onDoLikeClickSuccess(LikeClickBean likeClickBean);

    void onDoLikeClickError();

}
