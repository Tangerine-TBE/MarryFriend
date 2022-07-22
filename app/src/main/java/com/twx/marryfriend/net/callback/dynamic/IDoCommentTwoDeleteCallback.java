package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.CommentTwoDeleteBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoCommentTwoDeleteCallback extends IBaseCallback {

    void onDoCommentTwoDeleteSuccess(CommentTwoDeleteBean commentTwoDeleteBean);

    void onDoCommentTwoDeleteError();

}
