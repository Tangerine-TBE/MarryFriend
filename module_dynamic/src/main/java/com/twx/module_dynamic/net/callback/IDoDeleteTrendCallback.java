package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.CheckTrendBean;
import com.twx.module_dynamic.bean.DeleteTrendBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoDeleteTrendCallback extends IBaseCallback {

    void onDoDeleteTrendSuccess(DeleteTrendBean deleteTrendBean);

    void onDoDeleteTrendError();

}
