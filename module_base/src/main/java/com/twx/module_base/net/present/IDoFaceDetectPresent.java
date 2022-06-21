package com.twx.module_base.net.present;


import com.twx.module_base.net.callback.IDoFaceDetectCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoFaceDetectPresent extends IBasePresent<IDoFaceDetectCallback> {

    void doFaceDetect(Map<String, String> info);

}
