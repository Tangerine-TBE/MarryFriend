package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.CommentOneBean;
import com.twx.module_dynamic.bean.CommentTwoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetCommentTwoCallback extends IBaseCallback {

    void onGetCommentTwoSuccess(CommentTwoBean commentTwoBean);

    void onGetCommentTwoCodeError();

}
