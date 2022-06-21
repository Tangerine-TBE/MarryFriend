package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.FaceVerifyBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoFaceVerifyCallback extends IBaseCallback {

    void onDoFaceVerifySuccess(FaceVerifyBean faceVerifyBean);

    void onDoFaceVerifyError();

}
