package com.twx.marryfriend.net.present.dynamic;

import com.twx.marryfriend.net.callback.dynamic.IGetLikeListCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetLikeListPresent extends IBasePresent<IGetLikeListCallback> {

    void getLikeList(Map<String, String> info, Integer page, Integer size);

}
