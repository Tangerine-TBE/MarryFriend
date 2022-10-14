package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IGetCommentTwoCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetCommentTwoPresent extends IBasePresent<IGetCommentTwoCallback> {

    void getCommentTwo(Map<String, String> info);

}
