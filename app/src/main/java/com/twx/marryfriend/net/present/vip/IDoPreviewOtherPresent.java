package com.twx.marryfriend.net.present.vip;

import com.twx.marryfriend.net.callback.vip.IDoAliPayCallback;
import com.twx.marryfriend.net.callback.vip.IDoPreviewOtherCallback;
import com.twx.marryfriend.net.present.IBasePresent;

import java.util.Map;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public interface IDoPreviewOtherPresent extends IBasePresent<IDoPreviewOtherCallback> {

    void doPreviewOther(Map<String, String> info);

}
