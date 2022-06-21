package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoFaceDetectCallback;
import com.twx.marryfriend.net.callback.IDoLifeFaceDetectCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoLifeFaceDetectPresent extends IBasePresent<IDoLifeFaceDetectCallback> {

    void doLifeFaceDetect(Map<String, String> info);

}
