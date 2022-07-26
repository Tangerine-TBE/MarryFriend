package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.CommentTipBean;
import com.twx.marryfriend.bean.dynamic.LikeTipBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetCommentTipsCallback extends IBaseCallback {

    void onGetCommentTipsSuccess(CommentTipBean commentTipBean);

    void onGetCommentTipsError();

}
