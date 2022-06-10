package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.IDoIdentityVerifyCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoIdentityVerifyPresent extends IBasePresent<IDoIdentityVerifyCallback> {

    void doIdentityVerify(Map<String, String> info);

}
