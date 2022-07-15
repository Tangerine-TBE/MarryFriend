package com.twx.module_dynamic.net.present;

import com.twx.module_dynamic.net.callback.IGetLikeListCallback;
import com.twx.module_dynamic.net.callback.IGetMyTrendsListCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetLikeListPresent extends IBasePresent<IGetLikeListCallback> {

    void getLikeList(Map<String, String> info, Integer page, Integer size);

}
