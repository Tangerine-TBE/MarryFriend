package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.IDoViewHeadFaceCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoViewHeadFacePresent extends IBasePresent<IDoViewHeadFaceCallback> {

    void doViewHeadFace(Map<String, String> info);

}
