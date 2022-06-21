package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.UpdateDemandInfoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateDemandInfoCallback extends IBaseCallback {

    void onDoUpdateDemandInfoSuccess(UpdateDemandInfoBean updateDemandInfoBean);

    void onDoUpdateDemandInfoError();

}
