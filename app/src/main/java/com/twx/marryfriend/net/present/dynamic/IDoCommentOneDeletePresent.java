package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IDoCommentOneDeleteCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoCommentOneDeletePresent extends IBasePresent<IDoCommentOneDeleteCallback> {

    void doCommentOneDelete(Map<String, String> info);

}
