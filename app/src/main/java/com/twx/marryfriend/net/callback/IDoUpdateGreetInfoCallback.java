package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.UpdateDemandInfoBean;
import com.twx.marryfriend.bean.UpdateGreetInfoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateGreetInfoCallback extends IBaseCallback {

    void onDoUpdateGreetInfoSuccess(UpdateGreetInfoBean updateGreetInfoBean);

    void onDoUpdateGreetInfoError();

}
