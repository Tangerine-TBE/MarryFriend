package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.TotalCountBean;
import com.twx.module_dynamic.bean.TrendSaloonBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetTotalCountCallback extends IBaseCallback {

    void onGetTotalCountSuccess(TotalCountBean totalCountBean);

    void onGetTotalCountError();

}
