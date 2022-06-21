package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.UpdateProportionInfoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateProportionCallback extends IBaseCallback {

    void onDoUpdateProportionSuccess(UpdateProportionInfoBean updateProportionInfoBean);

    void onDoUpdateProportionError();

}
