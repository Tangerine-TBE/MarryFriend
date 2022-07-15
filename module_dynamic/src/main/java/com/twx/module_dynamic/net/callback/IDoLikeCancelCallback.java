package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.LikeCancelBean;
import com.twx.module_dynamic.bean.LikeClickBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoLikeCancelCallback extends IBaseCallback {

    void onDoLikeCancelSuccess(LikeCancelBean likeCancelBean);

    void onLikeCancelError();

}
