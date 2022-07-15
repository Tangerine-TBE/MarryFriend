package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.CheckTrendBean;
import com.twx.module_dynamic.bean.UploadTrendBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoCheckTrendCallback extends IBaseCallback {

    void onDoCheckTrendSuccess(CheckTrendBean checkTrendBean);

    void onDoCheckTrendError();

}
