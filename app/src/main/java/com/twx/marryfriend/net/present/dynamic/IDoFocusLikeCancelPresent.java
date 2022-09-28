package com.twx.marryfriend.net.present.dynamic;

import com.twx.marryfriend.net.callback.dynamic.IDoFocusLikeCancelCallback;
import com.twx.marryfriend.net.callback.dynamic.IDoLikeCancelCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoFocusLikeCancelPresent extends IBasePresent<IDoFocusLikeCancelCallback> {

    void doFocusLikeCancel(Map<String, String> info);

}
