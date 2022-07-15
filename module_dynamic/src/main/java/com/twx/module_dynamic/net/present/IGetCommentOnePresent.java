package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IGetCommentOneCallback;
import com.twx.module_dynamic.net.callback.IGetLikeListCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetCommentOnePresent extends IBasePresent<IGetCommentOneCallback> {

    void getCommentOne(Map<String, String> info);

}
