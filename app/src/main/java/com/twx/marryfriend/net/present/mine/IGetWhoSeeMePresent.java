package com.twx.marryfriend.net.present.mine;

import com.twx.marryfriend.net.callback.mine.IGetWhoSeeMeCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetWhoSeeMePresent extends IBasePresent<IGetWhoSeeMeCallback> {

    void getWhoSeeMe(Map<String, String> info);

}
