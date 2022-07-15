package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IDoCommentOneDeleteCallback;
import com.twx.module_dynamic.net.callback.IDoCommentTwoDeleteCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoCommentTwoDeletePresent extends IBasePresent<IDoCommentTwoDeleteCallback> {

    void doCommentTwoDelete(Map<String, String> info);

}
