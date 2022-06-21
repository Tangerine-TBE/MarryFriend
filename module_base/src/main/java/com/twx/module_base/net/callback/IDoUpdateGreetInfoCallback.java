package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.UpdateGreetInfoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateGreetInfoCallback extends IBaseCallback {

    void onDoUpdateGreetInfoSuccess(UpdateGreetInfoBean updateGreetInfoBean);

    void onDoUpdateGreetInfoError();

}
