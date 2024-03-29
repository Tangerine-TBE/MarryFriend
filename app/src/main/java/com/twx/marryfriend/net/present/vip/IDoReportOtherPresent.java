package com.twx.marryfriend.net.present.vip;

import com.twx.marryfriend.net.callback.vip.IDoReportOtherCallback;
import com.twx.marryfriend.net.callback.vip.IDoUploadFeedbackCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoReportOtherPresent extends IBasePresent<IDoReportOtherCallback> {

    void doReportOther(Map<String, String> info);

}
