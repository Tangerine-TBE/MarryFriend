package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IDoCommentOneCreateCallback;
import com.twx.module_dynamic.net.callback.IDoCommentOneDeleteCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoCommentOneDeletePresent extends IBasePresent<IDoCommentOneDeleteCallback> {

    void doCommentOneDelete(Map<String, String> info);

}
