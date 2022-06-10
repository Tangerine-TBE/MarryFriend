package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.UpdateMoreInfoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateMoreInfoCallback extends IBaseCallback {

    void onDoUpdateMoreInfoSuccess(UpdateMoreInfoBean updateMoreInfoBean);

    void onDoUpdateMoreInfoError();

}
