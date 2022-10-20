package com.twx.marryfriend.net.present.vip;

import com.twx.marryfriend.net.callback.IDoUpdateBaseInfoCallback;
import com.twx.marryfriend.net.callback.IDoUpdateDescribeCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoUpdateDescribePresent extends IBasePresent<IDoUpdateDescribeCallback> {

    void doUpdateDescribe(Map<String, String> info);

}
