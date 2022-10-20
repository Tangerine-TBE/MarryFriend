package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.vip.UpdateDescribeBean;
import com.twx.marryfriend.net.callback.IDoUpdateDescribeCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IDoUpdateDescribePresent;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public class doUpdateDescribePresentImpl implements IDoUpdateDescribePresent {

    public static doUpdateDescribePresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUpdateDescribeCallback> mCallback = new ArrayList<>();

    public doUpdateDescribePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUpdateDescribePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUpdateDescribePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUpdateDescribe(Map<String, String> info) {
        handlerLoading();
        mUserData.doUpdateDescribe(info, new Callback<UpdateDescribeBean>() {

            private UpdateDescribeBean mBody;

            @Override
            public void onResponse(Call<UpdateDescribeBean> call, Response<UpdateDescribeBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUpdateDescribeCallback callback : mCallback) {
                            callback.onDoUpdateDescribeSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateDescribeBean> call, Throwable t) {
                for (IDoUpdateDescribeCallback callback : mCallback) {
                    callback.onDoUpdateDescribeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoUpdateDescribeCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUpdateDescribeCallback iDoUpdateDescribeCallback) {
        if (!mCallback.contains(iDoUpdateDescribeCallback)) {
            mCallback.add(iDoUpdateDescribeCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUpdateDescribeCallback iDoUpdateDescribeCallback) {
        mCallback.remove(iDoUpdateDescribeCallback);
    }

}
