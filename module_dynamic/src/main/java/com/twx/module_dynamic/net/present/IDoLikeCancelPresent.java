package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IDoLikeCancelCallback;
import com.twx.module_dynamic.net.callback.IDoLikeClickCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoLikeCancelPresent extends IBasePresent<IDoLikeCancelCallback> {

    void doLikeCancel(Map<String, String> info);

}
