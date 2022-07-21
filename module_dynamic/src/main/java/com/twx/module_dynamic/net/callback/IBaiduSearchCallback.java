package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.SearchBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IBaiduSearchCallback extends IBaseCallback {

    void onBaiduSearchSuccess(SearchBean searchBean);

    void onBaiduSearchError();

}
