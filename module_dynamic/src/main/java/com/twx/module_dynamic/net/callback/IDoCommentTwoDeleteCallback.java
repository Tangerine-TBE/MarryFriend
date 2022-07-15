package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.CommentOneDeleteBean;
import com.twx.module_dynamic.bean.CommentTwoDeleteBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoCommentTwoDeleteCallback extends IBaseCallback {

    void onDoCommentTwoDeleteSuccess(CommentTwoDeleteBean commentTwoDeleteBean);

    void onDoCommentTwoDeleteError();

}
