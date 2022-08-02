package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.ViewHeadfaceBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoViewHeadFaceCallback extends IBaseCallback {

    void onDoViewHeadFaceSuccess(ViewHeadfaceBean viewHeadfaceBean);

    void onDoViewHeadFaceError();

}
