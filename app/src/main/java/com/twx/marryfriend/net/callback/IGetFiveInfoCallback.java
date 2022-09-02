package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.FiveInfoBean;
import com.twx.marryfriend.bean.VerifyCodeBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetFiveInfoCallback extends IBaseCallback {

    void onGetFiveInfoSuccess(FiveInfoBean fiveInfoBean);

    void onGetFiveInfoError();

}
