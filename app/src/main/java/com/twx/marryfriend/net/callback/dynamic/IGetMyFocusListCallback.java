package com.twx.marryfriend.net.callback.dynamic;

import com.twx.marryfriend.bean.dynamic.MyFocusBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetMyFocusListCallback extends IBaseCallback {

    void onGetMyFocusListSuccess(MyFocusBean myFocusBean);

    void onGetMyFocusListError();

}
