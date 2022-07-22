package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IDoCommentTwoDeleteCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoCommentTwoDeletePresent extends IBasePresent<IDoCommentTwoDeleteCallback> {

    void doCommentTwoDelete(Map<String, String> info);

}
