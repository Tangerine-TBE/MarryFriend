package com.twx.marryfriend.net.present.mine;

import com.twx.marryfriend.net.callback.mine.IDoDeleteBlackListCallback;
import com.twx.marryfriend.net.callback.mine.IGetBlackListCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoDeleteBlackListPresent extends IBasePresent<IDoDeleteBlackListCallback> {

    void doDeleteBlackList(Map<String, String> info);

}
