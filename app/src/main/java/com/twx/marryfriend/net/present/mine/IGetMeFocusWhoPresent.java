package com.twx.marryfriend.net.present.mine;

import com.twx.marryfriend.net.callback.mine.IGetMeFocusWhoCallback;
import com.twx.marryfriend.net.callback.mine.IGetMeSeeWhoCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetMeFocusWhoPresent extends IBasePresent<IGetMeFocusWhoCallback> {

    void getMeFocusWho(Map<String, String> info, Integer page);

}
