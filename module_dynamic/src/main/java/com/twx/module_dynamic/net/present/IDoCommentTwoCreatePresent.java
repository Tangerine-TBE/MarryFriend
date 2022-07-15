package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IDoCommentOneCreateCallback;
import com.twx.module_dynamic.net.callback.IDoCommentTwoCreateCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoCommentTwoCreatePresent extends IBasePresent<IDoCommentTwoCreateCallback> {

    void doCommentTwoCreate(Map<String, String> info);

}
