package com.twx.module_base.net.present;

import com.twx.module_base.net.callback.IDoUpdateDemandInfoCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUpdateDemandInfoPresent extends IBasePresent<IDoUpdateDemandInfoCallback> {

    void doUpdateDemandInfo(Map<String, String> info);

}
