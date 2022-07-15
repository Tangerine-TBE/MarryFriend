package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.CheckTrendBean;
import com.twx.module_dynamic.bean.MyFocusBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetMyFocusListCallback extends IBaseCallback {

    void onGetMyFocusListSuccess(MyFocusBean myFocusBean);

    void onGetMyFocusListError();

}
