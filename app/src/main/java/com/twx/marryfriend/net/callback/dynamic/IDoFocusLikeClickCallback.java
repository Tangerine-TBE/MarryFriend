package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.LikeClickBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoFocusLikeClickCallback extends IBaseCallback {

    void onDoFocusLikeClickSuccess(LikeClickBean likeClickBean);

    void onDoFocusLikeClickError();

}
