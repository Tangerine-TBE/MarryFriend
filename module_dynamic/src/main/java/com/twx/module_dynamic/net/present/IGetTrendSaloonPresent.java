package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IGetCommentOneCallback;
import com.twx.module_dynamic.net.callback.IGetTrendSaloonCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetTrendSaloonPresent extends IBasePresent<IGetTrendSaloonCallback> {

    void getTrendSaloon(Map<String, String> info);

}
