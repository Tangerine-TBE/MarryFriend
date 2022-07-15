package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.CommentOneDeleteBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoCommentOneDeleteCallback extends IBaseCallback {

    void onDoCommentOneDeleteSuccess(CommentOneDeleteBean commentOneDeleteBean);

    void onDoCommentOneDeleteError();

}
