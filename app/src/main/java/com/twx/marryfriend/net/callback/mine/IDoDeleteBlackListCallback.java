package com.twx.marryfriend.net.callback.mine;

import com.twx.marryfriend.bean.set.DeleteBlackListBean;
import com.twx.marryfriend.bean.vip.BlackListBean;
import com.twx.marryfriend.net.callback.IBaseCallback;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoDeleteBlackListCallback extends IBaseCallback {

    void onDoDeleteBlackListSuccess(DeleteBlackListBean deleteBlackListBean);

    void onDoDeleteBlackListCodeError();

}
