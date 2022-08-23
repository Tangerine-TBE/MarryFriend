package com.twx.marryfriend.net.callback.mine;

import com.twx.marryfriend.bean.mine.MeDiscussWhoBean;
import com.twx.marryfriend.bean.mine.MeFocusWhoBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetMeDiscussWhoCallback extends IBaseCallback {

    void onGetMeDiscussWhoSuccess(MeDiscussWhoBean meDiscussWhoBean);

    void onGetMeDiscussWhoCodeError();

}
