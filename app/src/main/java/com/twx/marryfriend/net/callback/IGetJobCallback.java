package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.JobBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetJobCallback extends IBaseCallback {

    void onGetJobSuccess(JobBean jobBean);

    void onGetJobError();

}
