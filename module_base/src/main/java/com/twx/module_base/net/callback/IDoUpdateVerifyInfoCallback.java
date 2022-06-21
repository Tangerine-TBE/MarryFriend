package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.UpdateVerifyInfoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateVerifyInfoCallback extends IBaseCallback {

    void onDoUpdateVerifyInfoSuccess(UpdateVerifyInfoBean updateVerifyInfoBean);

    void onDoUpdateVerifyInfoError();

}
