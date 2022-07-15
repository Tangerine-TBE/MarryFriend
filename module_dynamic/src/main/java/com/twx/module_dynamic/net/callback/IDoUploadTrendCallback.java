package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.PlaceSearchBean;
import com.twx.module_dynamic.bean.UploadTrendBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUploadTrendCallback extends IBaseCallback {

    void onDoUploadTrendSuccess(UploadTrendBean uploadTrendBean);

    void onDoUploadTrendError();

}
