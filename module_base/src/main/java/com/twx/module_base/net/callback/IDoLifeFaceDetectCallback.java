package com.twx.module_base.net.callback;

import com.twx.module_base.net.bean.FaceDetectBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoLifeFaceDetectCallback extends IBaseCallback {

    void onDoLifeFaceDetectSuccess(FaceDetectBean faceDetectBean);

    void onDoLifeFaceDetectError();

}
