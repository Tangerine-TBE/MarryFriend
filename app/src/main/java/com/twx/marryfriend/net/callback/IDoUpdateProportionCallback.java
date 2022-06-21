package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.UpdateProportionInfoBean;
import com.twx.marryfriend.bean.UploadPhotoBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateProportionCallback extends IBaseCallback {

    void onDoUpdateProportionSuccess(UpdateProportionInfoBean updateProportionInfoBean);

    void onDoUpdateProportionError();

}
