package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.CommentOneBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetCommentOneCallback extends IBaseCallback {

    void onGetCommentOneSuccess(CommentOneBean commentOneBean);

    void onGetCommentOneCodeError();

}
