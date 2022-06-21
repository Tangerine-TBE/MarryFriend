package com.twx.module_base.net.present;


import com.twx.module_base.net.callback.IDoFaceVerifyCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoFaceVerifyPresent extends IBasePresent<IDoFaceVerifyCallback> {

    void doFaceVerify(Map<String, String> info);

}
