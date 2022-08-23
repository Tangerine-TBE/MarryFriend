package com.twx.marryfriend.net.present.mine;

import com.twx.marryfriend.net.callback.mine.IGetWhoDiscussMeCallback;
import com.twx.marryfriend.net.callback.mine.IGetWhoFocusMeCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetWhoDiscussMePresent extends IBasePresent<IGetWhoDiscussMeCallback> {

    void getWhoDiscussMe(Map<String, String> info, Integer page);

}
