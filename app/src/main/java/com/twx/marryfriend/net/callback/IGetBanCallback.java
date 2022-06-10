package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.BanBean;
import com.twx.marryfriend.bean.SchoolBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetBanCallback extends IBaseCallback {

    void onGetBanSuccess(BanBean banBean);

    void onGetBanCodeError();

}
