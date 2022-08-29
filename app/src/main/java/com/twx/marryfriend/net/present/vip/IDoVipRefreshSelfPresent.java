package com.twx.marryfriend.net.present.vip;

import com.twx.marryfriend.net.callback.vip.IDoRefreshSelfCallback;
import com.twx.marryfriend.net.callback.vip.IDoVipRefreshSelfCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoVipRefreshSelfPresent extends IBasePresent<IDoVipRefreshSelfCallback> {

    void doVipRefreshSelf(Map<String, String> info);

}
