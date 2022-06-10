package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.UpdateVerifyInfoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateVerifyInfoCallback extends IBaseCallback {

    void onDoUpdateVerifyInfoSuccess(UpdateVerifyInfoBean updateVerifyInfoBean);

    void onDoUpdateVerifyInfoError();

}
