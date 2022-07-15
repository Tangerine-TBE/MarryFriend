package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IGetCommentOneCallback;
import com.twx.module_dynamic.net.callback.IGetCommentTwoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetCommentTwoPresent extends IBasePresent<IGetCommentTwoCallback> {

    void getCommentTwo(Map<String, String> info, Integer page, Integer size);

}
