package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.CommentOneCreateBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoCommentOneCreateCallback extends IBaseCallback {

    void onDoCommentOneCreateSuccess(CommentOneCreateBean commentOneCreateBean);

    void onDoCommentOneCreateError();

}
