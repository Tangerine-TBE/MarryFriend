package com.twx.marryfriend.net.present.vip;

import com.twx.marryfriend.net.callback.vip.IDoSVipRefreshSelfCallback;
import com.twx.marryfriend.net.callback.vip.IDoVipRefreshSelfCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoSVipRefreshSelfPresent extends IBasePresent<IDoSVipRefreshSelfCallback> {

    void doSVipRefreshSelf(Map<String, String> info);

}
