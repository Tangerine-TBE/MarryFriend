package com.twx.marryfriend.net.callback.dynamic;


import com.twx.marryfriend.bean.dynamic.TotalCountBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IGetTotalCountCallback extends IBaseCallback {

    void onGetTotalCountSuccess(TotalCountBean totalCountBean);

    void onGetTotalCountError();

}
