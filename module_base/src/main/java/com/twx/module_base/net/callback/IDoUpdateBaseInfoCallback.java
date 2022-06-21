package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.BaseInfoUpdateBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateBaseInfoCallback extends IBaseCallback {

    void onDoUpdateBaseInfoSuccess(BaseInfoUpdateBean baseInfoUpdateBean);

    void onDoUpdateBaseInfoError();

}
