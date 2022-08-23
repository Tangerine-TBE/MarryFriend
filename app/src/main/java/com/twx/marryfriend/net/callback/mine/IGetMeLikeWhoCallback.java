package com.twx.marryfriend.net.callback.mine;

import com.twx.marryfriend.bean.mine.MeFocusWhoBean;
import com.twx.marryfriend.bean.mine.MeLikeWhoBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetMeLikeWhoCallback extends IBaseCallback {

    void onGetMeLikeWhoSuccess(MeLikeWhoBean meLikeWhoBean);

    void onGetMeLikeWhoCodeError();

}
