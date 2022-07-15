package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.CommentOneBean;
import com.twx.module_dynamic.bean.LikeListBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetCommentOneCallback extends IBaseCallback {

    void onGetCommentOneSuccess(CommentOneBean commentOneBean);

    void onGetCommentOneCodeError();

}
