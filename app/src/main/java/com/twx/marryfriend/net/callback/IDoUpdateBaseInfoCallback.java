package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.BaseInfoUpdateBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateBaseInfoCallback extends IBaseCallback {

    void onDoUpdateBaseInfoSuccess(BaseInfoUpdateBean baseInfoUpdateBean);

    void onDoUpdateBaseInfoError();

}
