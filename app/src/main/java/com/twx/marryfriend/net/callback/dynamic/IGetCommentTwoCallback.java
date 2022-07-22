package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.CommentTwoBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetCommentTwoCallback extends IBaseCallback {

    void onGetCommentTwoSuccess(CommentTwoBean commentTwoBean);

    void onGetCommentTwoCodeError();

}
