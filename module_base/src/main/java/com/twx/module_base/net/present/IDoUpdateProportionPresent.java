package com.twx.module_base.net.present;

import com.twx.module_base.net.callback.IDoUpdateProportionCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUpdateProportionPresent extends IBasePresent<IDoUpdateProportionCallback> {

    void doUpdateProportion(Map<String, String> info);

}
