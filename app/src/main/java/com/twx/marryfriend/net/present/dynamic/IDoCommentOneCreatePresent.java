package com.twx.marryfriend.net.present.dynamic;

import com.twx.marryfriend.net.callback.dynamic.IDoCommentOneCreateCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoCommentOneCreatePresent extends IBasePresent<IDoCommentOneCreateCallback> {

    void doCommentOneCreate(Map<String, String> info);

}
