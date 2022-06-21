package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.UpdateMoreInfoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateMoreInfoCallback extends IBaseCallback {

    void onDoUpdateMoreInfoSuccess(UpdateMoreInfoBean updateMoreInfoBean);

    void onDoUpdateMoreInfoError();

}
