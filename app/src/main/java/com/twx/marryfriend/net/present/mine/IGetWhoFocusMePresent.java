package com.twx.marryfriend.net.present.mine;

import com.twx.marryfriend.net.callback.mine.IGetWhoFocusMeCallback;
import com.twx.marryfriend.net.callback.mine.IGetWhoSeeMeCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetWhoFocusMePresent extends IBasePresent<IGetWhoFocusMeCallback> {

    void getWhoFocusMe(Map<String, String> info, Integer page);

}
