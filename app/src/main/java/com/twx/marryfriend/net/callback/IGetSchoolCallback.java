package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.SchoolBean;
import com.twx.marryfriend.bean.VerifyCodeBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetSchoolCallback extends IBaseCallback {

    void onGetSchoolSuccess(SchoolBean schoolBean);

    void onGetSchoolCodeError();

}
