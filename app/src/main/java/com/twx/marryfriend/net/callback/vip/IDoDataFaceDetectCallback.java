package com.twx.marryfriend.net.callback.vip;

import com.twx.marryfriend.bean.FaceDetectBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoDataFaceDetectCallback extends IBaseCallback {

    void onDoDataFaceDetectSuccess(FaceDetectBean faceDetectBean);

    void onDoDataFaceDetectError();

}
