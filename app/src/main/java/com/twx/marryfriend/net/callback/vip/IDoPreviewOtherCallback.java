package com.twx.marryfriend.net.callback.vip;

import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.vip.PreviewOtherBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoPreviewOtherCallback extends IBaseCallback {

    void onDoPreviewOtherSuccess(PreviewOtherBean previewOtherBean);

    void onDoPreviewOtherError();

}
