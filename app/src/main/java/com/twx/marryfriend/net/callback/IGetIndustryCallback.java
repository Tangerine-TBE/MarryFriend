package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.IndustryBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetIndustryCallback extends IBaseCallback {

    void onGetIndustrySuccess(IndustryBean industryBean);

    void onGetIndustryError();

}
