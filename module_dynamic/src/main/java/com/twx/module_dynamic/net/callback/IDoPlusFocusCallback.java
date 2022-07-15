package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.MyTrendsListBean;
import com.twx.module_dynamic.bean.PlusFocusBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoPlusFocusCallback extends IBaseCallback {

    void onDoPlusFocusSuccess(PlusFocusBean plusFocusBean);

    void onDoPlusFocusError();

}
