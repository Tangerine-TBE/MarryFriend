package com.twx.marryfriend.net.callback.vip;

import com.twx.marryfriend.bean.vip.RefreshSelfBean;
import com.twx.marryfriend.bean.vip.UploadFeedbackBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUploadFeedbackCallback extends IBaseCallback {

    void onDoUploadFeedbackSuccess(UploadFeedbackBean uploadFeedbackBean);

    void onDoUploadFeedbackError();

}
