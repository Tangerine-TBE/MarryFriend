package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.FaceDetectBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoFaceDetectCallback extends IBaseCallback {

    void onDoFaceDetectSuccess(FaceDetectBean faceDetectBean);

    void onDoFaceDetectError();

}
