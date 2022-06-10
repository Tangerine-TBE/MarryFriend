package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.UpdateDemandInfoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateDemandInfoCallback extends IBaseCallback {

    void onDoUpdateDemandInfoSuccess(UpdateDemandInfoBean updateDemandInfoBean);

    void onDoUpdateDemandInfoError();

}
