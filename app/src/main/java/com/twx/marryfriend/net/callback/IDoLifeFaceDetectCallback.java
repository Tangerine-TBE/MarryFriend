package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.FaceDetectBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoLifeFaceDetectCallback extends IBaseCallback {

    void onDoLifeFaceDetectSuccess(FaceDetectBean faceDetectBean);

    void onDoLifeFaceDetectError();

}
