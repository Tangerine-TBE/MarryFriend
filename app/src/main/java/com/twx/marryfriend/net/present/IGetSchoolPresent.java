package com.twx.marryfriend.net.present;

import com.twx.marryfriend.net.callback.IGetSchoolCallback;
import com.twx.marryfriend.net.callback.IGetVerifyCodeCallback;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IGetSchoolPresent extends IBasePresent<IGetSchoolCallback> {

    void getSchool(Map<String, String> info);

}
