package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoFaceVerifyCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoFaceVerifyPresent extends IBasePresent<IDoFaceVerifyCallback> {

    void doFaceVerify(Map<String, String> info);

}
