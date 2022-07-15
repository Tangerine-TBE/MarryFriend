package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IDoCommentOneCreateCallback;
import com.twx.module_dynamic.net.callback.IDoLikeClickCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoCommentOneCreatePresent extends IBasePresent<IDoCommentOneCreateCallback> {

    void doCommentOneCreate(Map<String, String> info);

}
