package com.twx.marryfriend.net.present.dynamic;


import com.twx.marryfriend.net.callback.dynamic.IDoFocusLikeClickCallback;
import com.twx.marryfriend.net.callback.dynamic.IDoLikeClickCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoFocusLikeClickPresent extends IBasePresent<IDoFocusLikeClickCallback> {

    void doFocusLikeClick(Map<String, String> info);

}
