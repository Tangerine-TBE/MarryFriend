package com.twx.marryfriend.net.callback.mine;

import com.twx.marryfriend.bean.mine.MeSeeWhoBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetMeSeeWhoCallback extends IBaseCallback {

    void onGetMeSeeWhoSuccess(MeSeeWhoBean meSeeWhoBean);

    void onGetMeSeeWhoCodeError();

}
