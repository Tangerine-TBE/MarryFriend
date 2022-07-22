package com.twx.marryfriend.net.present.dynamic;

import com.twx.marryfriend.net.callback.dynamic.IDoLikeCancelCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoLikeCancelPresent extends IBasePresent<IDoLikeCancelCallback> {

    void doLikeCancel(Map<String, String> info);

}
