package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.CancelFocusBean;
import com.twx.module_dynamic.bean.PlusFocusBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoCancelFocusCallback extends IBaseCallback {

    void onDoCancelFocusSuccess(CancelFocusBean cancelFocusBean);

    void onDoCancelFocusError();

}
