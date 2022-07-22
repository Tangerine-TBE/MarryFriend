package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IGetCommentOneCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetCommentOnePresent extends IBasePresent<IGetCommentOneCallback> {

    void getCommentOne(Map<String, String> info);

}
