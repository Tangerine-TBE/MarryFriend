package com.twx.marryfriend.net.present.mine;

import com.twx.marryfriend.net.callback.mine.IGetWhoFocusMeCallback;
import com.twx.marryfriend.net.callback.mine.IGetWhoLikeMeCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetWhoLikeMePresent extends IBasePresent<IGetWhoLikeMeCallback> {

    void getWhoLikeMe(Map<String, String> info);

}
