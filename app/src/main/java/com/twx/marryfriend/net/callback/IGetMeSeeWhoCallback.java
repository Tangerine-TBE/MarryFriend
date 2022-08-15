package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.MeSeeWhoBean;
import com.twx.marryfriend.bean.WhoSeeMeBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetMeSeeWhoCallback extends IBaseCallback {

    void onGetMeSeeWhoSuccess(MeSeeWhoBean meSeeWhoBean);

    void onGetMeSeeWhoCodeError();

}
