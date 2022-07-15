package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.CommentOneCreateBean;
import com.twx.module_dynamic.bean.CommentTwoCreateBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoCommentTwoCreateCallback extends IBaseCallback {

    void onDoCommentTwoCreateSuccess(CommentTwoCreateBean commentTwoCreateBean);

    void onDoCommentTwoCreateError();

}
