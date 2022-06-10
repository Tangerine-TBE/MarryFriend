package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetIndustryCallback;
import com.twx.marryfriend.net.callback.IGetSchoolCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetIndustryPresent extends IBasePresent<IGetIndustryCallback> {

    void getIndustry(Map<String, String> info);

}
