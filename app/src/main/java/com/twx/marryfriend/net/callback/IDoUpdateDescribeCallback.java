package com.twx.marryfriend.net.callback;

import com.twx.marryfriend.bean.vip.UpdateDescribeBean;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public interface IDoUpdateDescribeCallback extends IBaseCallback {

    void onDoUpdateDescribeSuccess(UpdateDescribeBean updateDescribeBean);

    void onDoUpdateDescribeError();

}
