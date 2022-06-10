package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoFaceDetectCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoFaceDetectPresent extends IBasePresent<IDoFaceDetectCallback> {

    void doFaceDetect(Map<String, String> info);

}
