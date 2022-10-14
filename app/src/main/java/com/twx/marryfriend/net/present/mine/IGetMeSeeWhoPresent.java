package com.twx.marryfriend.net.present.mine;

import com.twx.marryfriend.net.callback.mine.IGetMeSeeWhoCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetMeSeeWhoPresent extends IBasePresent<IGetMeSeeWhoCallback> {

    void getMeSeeWho(Map<String, String> info);

}
