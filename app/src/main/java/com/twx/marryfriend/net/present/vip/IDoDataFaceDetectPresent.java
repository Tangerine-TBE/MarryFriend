package com.twx.marryfriend.net.present.vip;

import com.twx.marryfriend.net.callback.IDoFaceDetectCallback;
import com.twx.marryfriend.net.callback.vip.IDoDataFaceDetectCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoDataFaceDetectPresent extends IBasePresent<IDoDataFaceDetectCallback> {

    void doDataFaceDetect(Map<String, String> info);

}
