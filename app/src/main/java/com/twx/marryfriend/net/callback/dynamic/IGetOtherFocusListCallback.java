package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.OtherFocusBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetOtherFocusListCallback extends IBaseCallback {

    void onGetOtherFocusListSuccess(OtherFocusBean otherFocusBean);

    void onGetOtherFocusListError();

}
