package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IDoUpdateGreetInfoCallback;
import com.twx.marryfriend.net.callback.IDoUpdateProportionCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUpdateProportionPresent extends IBasePresent<IDoUpdateProportionCallback> {

    void doUpdateProportion(Map<String, String> info);

}
