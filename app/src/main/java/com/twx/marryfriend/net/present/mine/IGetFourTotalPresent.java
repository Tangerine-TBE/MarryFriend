package com.twx.marryfriend.net.present.mine;

import com.twx.marryfriend.net.callback.mine.IGetFourTotalCallback;
import com.twx.marryfriend.net.callback.mine.IGetWhoSeeMeCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetFourTotalPresent extends IBasePresent<IGetFourTotalCallback> {

    void getFourTotal(Map<String, String> info);

}
