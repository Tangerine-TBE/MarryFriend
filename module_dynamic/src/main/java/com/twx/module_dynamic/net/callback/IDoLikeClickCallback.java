package com.twx.module_dynamic.net.callback;

import com.twx.module_dynamic.bean.CheckTrendBean;
import com.twx.module_dynamic.bean.LikeClickBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoLikeClickCallback extends IBaseCallback {

    void onDoLikeClickSuccess(LikeClickBean likeClickBean);

    void onDoLikeClickError();

}
