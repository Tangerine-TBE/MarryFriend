package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.FaceVerifyBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoFaceVerifyCallback extends IBaseCallback {

    void onDoFaceVerifySuccess(FaceVerifyBean faceVerifyBean);

    void onDoFaceVerifyError();

}
