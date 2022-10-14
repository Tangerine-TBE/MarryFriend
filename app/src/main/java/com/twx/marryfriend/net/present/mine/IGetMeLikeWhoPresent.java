package com.twx.marryfriend.net.present.mine;

import com.twx.marryfriend.net.callback.mine.IGetMeFocusWhoCallback;
import com.twx.marryfriend.net.callback.mine.IGetMeLikeWhoCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetMeLikeWhoPresent extends IBasePresent<IGetMeLikeWhoCallback> {

    void getMeLikeWho(Map<String, String> info);

}
