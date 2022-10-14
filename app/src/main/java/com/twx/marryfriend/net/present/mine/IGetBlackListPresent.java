package com.twx.marryfriend.net.present.mine;

import com.twx.marryfriend.net.callback.mine.IGetBlackListCallback;
import com.twx.marryfriend.net.callback.vip.IGetCoinRecordCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetBlackListPresent extends IBasePresent<IGetBlackListCallback> {

    void getBlackList(Map<String, String> info);

}
