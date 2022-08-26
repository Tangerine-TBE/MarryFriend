package com.twx.marryfriend.net.present.dynamic;



import com.twx.marryfriend.net.callback.dynamic.IDoCancelFocusCallback;
import com.twx.marryfriend.net.callback.dynamic.IDoDeleteTipsCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoDeleteTipsPresent extends IBasePresent<IDoDeleteTipsCallback> {

    void doDeleteTips(Map<String, String> info);

}
