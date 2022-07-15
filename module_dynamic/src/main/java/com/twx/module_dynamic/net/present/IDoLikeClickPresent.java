package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IDoCheckTrendCallback;
import com.twx.module_dynamic.net.callback.IDoLikeClickCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoLikeClickPresent extends IBasePresent<IDoLikeClickCallback> {

    void doLikeClick(Map<String, String> info);

}
