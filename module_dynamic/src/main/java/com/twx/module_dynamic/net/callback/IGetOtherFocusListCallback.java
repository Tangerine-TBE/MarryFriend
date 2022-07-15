package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.MyFocusBean;
import com.twx.module_dynamic.bean.OtherFocusBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetOtherFocusListCallback extends IBaseCallback {

    void onGetOtherFocusListSuccess(OtherFocusBean otherFocusBean);

    void onGetOtherFocusListError();

}
